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
import gov.nasa.arc.mct.scenario.component.TagCapability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVRenderer {
	private static final String CHILD_PREFIX = "Reference ";
	private static final String TAG_PREFIX = "Tag ";
	
	private Collection<String> headers = new ArrayList<String>();
	private List<String> components = new ArrayList<String>();
	private Map<String, Map<String, String>> values = 
			new HashMap<String, Map<String, String>>();
	private int maxChildren = 0;
	private int maxTags = 0;

	public CSVRenderer(Collection<AbstractComponent> components) {
		for (AbstractComponent ac : components) {
			add(ac);
		}
		addTagHeaders();
		addChildHeaders();
	}
	
	public CSVRenderer(AbstractComponent ac) {
		this(Collections.singleton(ac));
	}
	
	public String render() {
		StringBuilder builder = new StringBuilder("");
		String[] row = new String[headers.size()];

		renderRow(builder, headers.toArray(row));
		
		for (String id : components) {
			renderRow(builder, id, row);
		}
		
		return builder.toString();
	}
	
	public int getRowCount() {
		return components.size();
	}
	
	public String renderHeaders() {
		StringBuilder b = new StringBuilder();		
		renderRow(b, headers.toArray(new String[headers.size()]));		
		return b.toString();	
	}
	
	public String renderRow(int index) {
		if (index < 0 || index >= getRowCount()) {
			throw new IllegalArgumentException();
		}
		
		StringBuilder b = new StringBuilder();		
		renderRow(b, components.get(index), new String[headers.size()]);		
		return b.toString();
	}
	
	private void renderRow(StringBuilder b, String id, String[] row) {
		Map<String, String> map = values.get(id);
		int i = 0;
		for (String description : headers) {
			row[i++] = map.get(description);
		}
		renderRow(b, row);		
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
			addProperty(id, "Base Displayed Name", ac.getDisplayName());
			addProperty(id, "Component Type", ac.getComponentTypeID());
			addProperty(id, "MCT Id", ac.getComponentId());
			
			// Add values from property descriptors
			List<PropertyDescriptor> descriptors = 
					ac.getFieldDescriptors();
			if (descriptors != null) {
				for (PropertyDescriptor pd : descriptors) {
					addPropertyDescriptor(id, pd);
				}
			}
			
			// Look up tags explicitly
			Collection<TagCapability> tags = 
					ac.getCapabilities(TagCapability.class);
			if (tags != null) {
				int t = 0;
				for (TagCapability tag : tags) {
					map.put(tagPrefix(t++), tag.getTag());
					maxTags = Math.max(maxTags, t);
				}
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
	
	private void addProperty(String id, String description, String value) {
		if (description != null && value != null) {
			if (!headers.contains(description)) {
				headers.add(description);
			}
			values.get(id).put(description, value);			
		}
	}

	private void addPropertyDescriptor(String id, PropertyDescriptor pd) {
		String description = null;
		String value = null;
		
		try {
			description = pd.getShortDescription();
			value = pd.getPropertyEditor().getAsText();
		} catch (IllegalArgumentException iae) {
			// If getAsText is unsupported for a property, skip it
		}
		
		addProperty(id, description, value);		
	}

	// Tags and children are handled specially, to ensure they 
	// appear grouped and in order.
	// These should be consolidated if more such properties emerge...
	
	private String tagPrefix(int index) {
		return TAG_PREFIX + (index + 1);
	}
	
	private String childPrefix(int index) {
		return CHILD_PREFIX + (index + 1);
	}
	
	private void addTagHeaders() {
		for (int i = 0; i < maxTags; i++) {
			headers.add(tagPrefix(i));
		}
	}
	
	private void addChildHeaders() {
		for (int i = 0; i < maxChildren; i++) {
			headers.add(childPrefix(i));
		}
	}
}
