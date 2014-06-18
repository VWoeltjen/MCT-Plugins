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
package gov.nasa.arc.mct.csvexport.actions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.csvexport.component.CSVExportCapability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for rendering MCT objects to CSV format.
 *  
 * The default CSV text are:
 * - Base display name, type, MCT ID.
 * - Any object-specific fields (as seen in Info View).
 * - Any Tags (as strings).
 * - Component IDs of referenced child components.
 * 
 * @author vwoeltje
 */
public class CSVRenderer {
	private static final String CHILD_PREFIX = 
			BundleAccess.BUNDLE.getString("csv_child_prefix");
	// private static final String TAG_PREFIX = BundleAccess.BUNDLE.getString("csv_tag_prefix");
	
	private Collection<String> headers = new ArrayList<String>();
	private List<String> components = new ArrayList<String>();
	private Map<String, Map<String, String>> values = 
			new HashMap<String, Map<String, String>>();
	private int maxChildren = 0;
	// private int maxTags = 0;

	/**
	 * Create a renderer which will express the specified
	 * group of components in CSV format. This includes all of 
	 * their children.
	 * 
	 * Data about components used to support CSV rendering 
	 * will be assembled at the time of the constructor call, 
	 * including visitation of all children. As such, this 
	 * should not be called from a user interface thread. 
	 * 
	 * @param components the components to render
	 */
	public CSVRenderer(Collection<AbstractComponent> components) {
		for (AbstractComponent ac : components) {
			add(ac);
		}
		// currently not using tag
		// addTagHeaders();
		addChildHeaders();
	}
	
	/**
	 * Create a renderer which will express the specified 
	 * component in CSV format. This includes all of 
	 * its children.
	 * 
	 * Data about components used to support CSV rendering 
	 * will be assembled at the time of the constructor call, 
	 * including visitation of all children. As such, this 
	 * should not be called from a user interface thread. 
	 * 
	 * @param ac the component to render
	 */
	public CSVRenderer(AbstractComponent ac) {
		this(Collections.singleton(ac));
	}
	
	/**
	 * Return a full CSV representation of all 
	 * components specified in the constructor. 
	 * The first line of the returned string will 
	 * contain column headers.
	 * 
	 * This is potentially a very large String depending 
	 * on the number of components involved; it may 
	 * make sense to render the CSV piece-wise using 
	 * {@link #renderHeaders()} and {@link #renderRow(int)}, 
	 * particularly if writing to a stream.
	 * 
	 * @return a CSV representation of all components
	 */
	public String render() {
		StringBuilder builder = new StringBuilder("");
		String[] row = new String[headers.size()];

		renderRow(builder, headers.toArray(row));
		
		for (String id : components) {
			renderRow(builder, id, row);
		}
		
		return builder.toString();
	}
	
	/**
	 * Get the number of rows in the resulting CSV text. This does not 
	 * include the row which contains column headers.
	 * 
	 * @return the number of non-header rows in the rendered CSV 
	 */
	public int getRowCount() {
		return components.size();
	}
	
	/**
	 * Get a line of CSV text containing column headers. 
	 * 
	 * @return comma-separated column headers
	 */
	public String renderHeaders() {
		StringBuilder b = new StringBuilder();		
		renderRow(b, headers.toArray(new String[headers.size()]));		
		return b.toString();	
	}
	
	/**
	 * Return a specific row (corresponding to one MCT object) in 
	 * CSV format.
	 * 
	 * The meaning of columns in each row is given in column 
	 * headers, retrieved using {@link #renderHeaders()}.
	 * 
	 * @param index the row's index; 0 <= index < {@link #getRowCount()}
	 * @return CSV text for the specified row
	 */
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
			
			CSVExportCapability capability = ac.getCapability(CSVExportCapability.class);
			if (capability != null) {			
				String[] values = capability.getValues();
				String[] headers = capability.getHeaders();				
				for (int i = 0; i < capability.getNumberOfColumns(); i++) {
					addProperty(id, headers[i], values[i]);
				}
				renderChild(ac, map);
			} else {
				renderDefaultProperty(ac, id, map);
				
				// not include TagCapability temporarily
				//renderTag();
				
				renderChild(ac, map);
			}
		}
	}
	
	private void renderDefaultProperty(AbstractComponent ac, String id, Map<String, String> map) {		
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
	
	/** 
	private void renderTag() {		
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
	} */
	
	private void renderChild(AbstractComponent ac, Map<String, String> map) {
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
	
	/** private String tagPrefix(int index) {
		return TAG_PREFIX + (index + 1);
	}
	
	private void addTagHeaders() {
		for (int i = 0; i < maxTags; i++) {
			headers.add(tagPrefix(i));
		}
	} */
	
	private String childPrefix(int index) {
		return CHILD_PREFIX + (index + 1);
	}
	
	private void addChildHeaders() {
		for (int i = 0; i < maxChildren; i++) {
			headers.add(childPrefix(i));
		}
	}
}
