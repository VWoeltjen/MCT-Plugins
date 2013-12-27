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

public interface CostCapability {
	/**
	 * Get the name of this cost.
	 * @return the name of this cost
	 */
	public String getName();
	
	/**
	 * Get the units used for this cost (as a string)
	 * @return the units used by this cost
	 */
	public String getUnits();

	/**
	 * Assess this cost at the specified time. 
	 * @param time the time, in milliseconds since start of timeline
	 * @return the cost at the specified time
	 */
	public double getValue();
	
	/**
	 * Set the value associated with this cost.
	 * May throw an exception if setting is not supported.
	 * @throws UnsupportedOperationException
	 */
	public void setValue(double value);	

	/**
	 * Indicates whether or not setValue will be supported.
	 * @return true if setValue is supported; otherwise false.
	 */
	public boolean isMutable();
}
