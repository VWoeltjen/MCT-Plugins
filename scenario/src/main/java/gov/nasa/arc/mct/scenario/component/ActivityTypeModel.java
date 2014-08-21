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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Container for an Activity Type's model. (contains power, comms)
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityTypeModel {
	private Map<String, Double> costs = new HashMap<String, Double> ();
	
	public ActivityTypeModel() {
		costs.put("COMM", 0.0);
		costs.put("POWER", 0.0);
	}
	
	private String url = "";

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void set(String typeName, double value) {
		costs.put(typeName, value);
	}
	
	public double get(String typeName) {
		if (costs.get(typeName) == null) {
			return 0.0;
		} else {
			return (Double)costs.get(typeName);
		}
	}
	
	public Collection<String> getKeys() {
		return costs.keySet();
	}
	
	public Collection<Double> getValues() {
		return costs.values();
	}
}
