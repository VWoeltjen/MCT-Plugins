/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.scenario.actions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.util.LinkedHashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CSVExporter {
	private static final String CHILD_PREFIX = "Reference ";
	
	private Set<String> headers = new LinkedHashSet<String>();
	private Set<String> components = new LinkedHashSet<String>();
	private Map<String, Map<String, String>> values = 
			new HashMap<String, Map<String, String>>();
	private int maxChildren = 0;
	
	public CSVExporter(AbstractComponent ac) {
		add(ac);
		addChildHeaders();
	}
	
	public String render() {
		List<String> headers = reverse(this.headers);
		StringBuilder builder = new StringBuilder("");
		String[] row = new String[headers.size()];

		renderRow(builder, headers.toArray(row));
		
		for (String id : reverse(components)) {
			Map<String, String> map = values.get(id);
			int i = 0;
			for (String description : headers) {
				row[i++] = map.get(description);
			}
			renderRow(builder, row);
		}
		
		return builder.toString();
	}
	
	private void renderRow(StringBuilder builder, String[] row) {
		for (int i = 0; i < row.length; i++) {
			// Insert comma after first elements
			if (i > 0) {
				builder.append(',');
			}

			// Append the value to the StringBuilder, with comma
			String value = row[i];
			if (value == null) {
				// Do nothing - leave empty
			} else if (value.contains(",") || values.containsKey("\n")) {
				builder.append('"');
				builder.append(value);
				builder.append('"');
			} else {
				builder.append(value);
			}
		}
		
		builder.append('\n');
	}
	
	private void add(AbstractComponent ac) {
		String id = ac.getComponentId();
		if (!components.contains(id)) {
			// Create new map for this component's values, 
			// add entries to related data structures.
			Map<String, String> map = new HashMap<String, String>();
			components.add(id);
			values.put(id, map);
			
			// Add core common properties
			
			
			// Add values from property descriptors
			for (PropertyDescriptor pd : ac.getFieldDescriptors()) {
				addPropertyDescriptor(id, pd);
			}
			
			// Store references to children
			// Headers are not added until later, to ensure these
			// come at the end.
			int i = 0;
			for (AbstractComponent child : ac.getComponents()) {
				map.put(childPrefix(i++), child.getComponentId());
				maxChildren = Math.max(maxChildren, i);
				add(child);
			}
		}
	}
	
	private String childPrefix(int index) {
		return CHILD_PREFIX + (index + 1);
	}
	
	private void addProperty(String id, String description, String value) {
		if (description != null && value != null) {
			headers.add(description);
			values.get(id).put(description, value);			
		}
	}

	private void addPropertyDescriptor(String id, PropertyDescriptor pd) {
		Map<String, String> map = values.get(id);
		String description = null;
		String value = null;
		
		try {
			description = pd.getShortDescription();
			value = pd.getPropertyEditor().getAsText();
		} catch (IllegalArgumentException iae) {
			// If getAsTextis unsupported for a property, skip it
		}
		
		addProperty(id, description, value);		
	}
	
	private void addChildHeaders() {
		for (int i = 0; i < maxChildren; i++) {
			headers.add(childPrefix(i));
		}
	}
	
	
	// Utility method to reverse collection from iteration order.
	// Used because LinkedHashSet retains insertion order with 
	// most recent first.
	private <T> List<T> reverse (Collection<T> collection) {
		List<T> reversed = new ArrayList<T>(collection.size());
		reversed.addAll(collection);
		Collections.reverse(reversed);
		return reversed;
	}
}
