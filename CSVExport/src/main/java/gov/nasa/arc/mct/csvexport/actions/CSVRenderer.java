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
import gov.nasa.arc.mct.csvexport.component.CSVExportCapability;

import java.util.Collection;
import java.util.Collections;

/**
 * Responsible for rendering MCT objects to CSV format.
 * The format is defined either in component's CSVExportCapability,
 * or in DefaultCSVExportCapability.
 * 
 * @author jdong
 */
public class CSVRenderer {

	private CSVExportCapability capability;

	/**
	 * Create a renderer which will express the specified
	 * group of components in CSV format. This includes 
	 * all of their children.
	 * 
	 * If only export one component in CSV format and the component
	 * has CSVExportCapability, export it as the format specified in
	 * its CSVExportCapability. Or else, use the default CSV format
	 * as specified in DefaultCSVExportCapability.
	 * If multiple components are being exported in CSV format, use 
	 * the format defined in DefaultCSVExportCapability.
	 * 
	 * @param components the components to render
	 */
	public CSVRenderer(Collection<AbstractComponent> components) {
		if ( components.size() == 1 ) {
			AbstractComponent ac = components.iterator().next();
			capability = ac.getCapability(CSVExportCapability.class);
		}
		if ( capability == null ) {
			capability = new DefaultCSVExportCapability(components);
		}
	}

	/**
	 * Create a renderer which will express the specified 
	 * component in CSV format. This includes all of 
	 * its children.
	 * 
	 * If only export one component in CSV format and the component
	 * has CSVExportCapability, export it as the format specified in
	 * its CSVExportCapability. Or else, use the default CSV format
	 * as specified in DefaultCSVExportCapability.
	 * If multiple components are being exported in CSV format, use 
	 * the format defined in DefaultCSVExportCapability.
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

		renderRow(builder, capability.getHeaders());
		
		for (int i = 0; i < capability.getRowCount(); i++) {
			renderRow(builder, capability.getValue(i));
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
		return capability.getRowCount();
	}
	
	/**
	 * Get a line of CSV text containing column headers. 
	 * 
	 * @return comma-separated column headers
	 */
	public String renderHeaders() {
		StringBuilder b = new StringBuilder();		
		renderRow(b, capability.getHeaders());		
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
		if (index < 0 || index >= capability.getRowCount()) {
			throw new IllegalArgumentException();
		}
		
		StringBuilder b = new StringBuilder();		
		renderRow(b, capability.getValue(index));		
		return b.toString();
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
			} else if (value.contains(",")) {
				builder.append('"');
				builder.append(value);
				builder.append('"');
			} else {
				builder.append(value);
			}
		}
		
		builder.append('\n');
	}
}
