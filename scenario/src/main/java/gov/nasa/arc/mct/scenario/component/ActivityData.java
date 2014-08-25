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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Underlying model data for Activity components.
 * These includes costs (power/data), time/duration, and type.
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityData {
	
	// keep the instance variable for backward compatability
	private double power;
	private double comm;
	private String type = "";
	private String notes = "";
	private Date startDate;
	private Date endDate;
	private String url = "";
	private String procedureUrl = "";

	private Map<String, String> properties = new HashMap<String, String> ();
	private static final String DEFAULT_VALUE = "";
	
	public ActivityData() {
		init();
	}
	
	private void init() {
		properties.put("COMM", DEFAULT_VALUE);
		properties.put("POWER", DEFAULT_VALUE);
		properties.put("type", DEFAULT_VALUE);
		properties.put("notes", DEFAULT_VALUE);
		properties.put("startTime", DEFAULT_VALUE);
		properties.put("endTime", DEFAULT_VALUE);
		properties.put("url", DEFAULT_VALUE);
		properties.put("procedureUrl", DEFAULT_VALUE);
	}
	
	public String getValue(String key) {
		String value = "";
		
		if (properties.containsKey(key)) {
			value = properties.get(key);
		}
		
		if (value.equals(DEFAULT_VALUE)) value = readFields(key);
		if (value != DEFAULT_VALUE) setValue(key, value); // save the value from older ActivityData to the map
		
		return value;
	}
	
	// checkout whether reading from older version of Activity Data
	private String readFields(String key) {
		String value = "";
		
		if (key.equals("POWER")) value = String.valueOf(power);
		else if (key.equals("COMM")) value = String.valueOf(comm);
		else if (key.equals("type")) value = type;
		else if (key.equals("notes")) value = notes;
		else if (key.equals("startTime")) value = String.valueOf(startDate.getTime());
		else if (key.equals("endTime")) value = String.valueOf(endDate.getTime());
		else if (key.equals("url")) value = url;
		else if (key.equals("procedureUrl")) value = procedureUrl;		
		return value;
	}
	
	public void setValue(String key, String value) {
		properties.put(key, value);		
	}

	public long getDurationTime()
	{
		long endTime = Long.parseLong(properties.get("endTime"));
		long startTime = Long.parseLong(properties.get("startTime"));
		return endTime - startTime;
	}
	
	public void setDurationTime(long duration)
	{		
		long startTime = Long.parseLong(properties.get("startTime"));
		properties.put("endTime", String.valueOf(startTime + duration));
	}
	
}
