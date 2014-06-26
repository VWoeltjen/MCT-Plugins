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
package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.csvexport.component.CSVExportCapability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


public class ScenarioCSVExportCapability implements CSVExportCapability {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("Bundle");  ;
	
	private static final String CHILD_PREFIX = BUNDLE.getString("csv_child_prefix");
	private static final String TAG_PREFIX = BUNDLE.getString("csv_tag_prefix");
	
	private Collection<String> headers = new ArrayList<String>();
	private List<String> components = new ArrayList<String>(); // store components' ids
	// store each component's id and its corresponding CSV content
	private Map<String, Map<String, String>> values = 
			new HashMap<String, Map<String, String>>(); 
	private int maxChildren = 0;
	private int maxTags = 0;

	/**
	 * Express the specified group of components in CSV format. 
	 * This includes all of their children.
	 * 
	 * Data about components used to support CSV rendering 
	 * will be assembled at the time of the constructor call, 
	 * including visitation of all children. As such, this 
	 * should not be called from a user interface thread. 
	 * 
	 * @param components the components to render
	 */
	public ScenarioCSVExportCapability(Collection<AbstractComponent> components) {
		for (AbstractComponent ac : components) {
			add(ac);
		}
		// currently not using tag
		addTagHeaders();
		addChildHeaders();
	}	
	
	public ScenarioCSVExportCapability(AbstractComponent component) {
		this(Collections.singleton(component));		
	}
	
	/**
	 * add CSV content of the current component and its children to values.
	 * note that for each component, its id is stored in components. Its id
	 * and csv content are stored in values.
	 */
	private void add(AbstractComponent ac) {
		String id = ac.getComponentId();
		if (!components.contains(id)) {
			// Create new map for this component's values,
			// add entries to related data structures.
			Map<String, String> map = new HashMap<String, String>();
			components.add(id);
			values.put(id, map);

			addDefaultProperty(ac, id, map);
			
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
			
			addChild(ac, map);			
		}
	}
	
	private void addDefaultProperty(AbstractComponent ac, String id, Map<String, String> map) {		
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
	
	private void addChild(AbstractComponent ac, Map<String, String> map) {
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
	
	private String childPrefix(int index) {
		return CHILD_PREFIX + (index + 1);
	}
	
	private void addChildHeaders() {
		for (int i = 0; i < maxChildren; i++) {
			headers.add(childPrefix(i));
		}
	}
	
	@Override
	public int getRowCount() {
		return components.size();
	}
	
	@Override
	public int getColumnCount() {
		return headers.size();
	}
	
	@Override
	public String[] getHeaders() {	
		String[] headerArray = new String[headers.size()];
		headers.toArray(headerArray);	
		return headerArray;
	}

	@Override
	public String[] getValue(int row) {
		String[] value = new String[headers.size()];
		Map<String, String> map = values.get(components.get(row));
		int i = 0;
		for (String description : headers) {
			value[i++] = map.get(description);
		}
		return value;
	}
	

	// Tags and children are handled specially, to ensure they 
	// appear grouped and in order.
	// These should be consolidated if more such properties emerge...
	
	private String tagPrefix(int index) {
		return TAG_PREFIX + (index + 1);
	}
	
	private void addTagHeaders() {
		for (int i = 0; i < maxTags; i++) {
			headers.add(tagPrefix(i));
		}
	}
}

