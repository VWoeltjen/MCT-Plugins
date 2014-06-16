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
package gov.nasa.arc.mct.csvexport.component;


/**
 * Represents some csvexport associated with a component. Components with 
 * csvexport have certain columns.
 * 
 * This should be exposed via the getCapability method of a component.
 * 
 * @author jdong
 *
 */
public interface CSVExportCapability {
	
	/**
	 * get the value of the content for CSVExport .
	 * CSV exports the content associated with all selected components.
	 * The return value is a two dimensional array. Each row represent 
	 * data associated with a component.
	 *
	 */
	public String[][] getValues();
	
	public int getNumberOfColumns();	
}
