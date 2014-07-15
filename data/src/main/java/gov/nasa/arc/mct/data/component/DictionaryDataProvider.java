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
package gov.nasa.arc.mct.data.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import gov.nasa.arc.mct.api.feed.DataProvider;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.gui.FeedView;

/**
 * This class takes-in data-feeds and then grabs the feed associated with a Data component (it does
 * this by looking for the unique identifier that DataComponent placed into its feeds).  With the feedID, 
 * this class recognizes and provides the data for it.
 *
 * @author jdong2
 *
 */
public class DictionaryDataProvider implements DataProvider {

	@Override
	public Map<String, SortedMap<Long, Map<String, String>>> getData(Set<String> feedIDs, long startTime, long endTime, TimeUnit timeUnit) {
		Map<String, SortedMap<Long, Map<String, String>>> out = new HashMap<String, SortedMap<Long, Map<String, String>>>();
		for(String feedID : feedIDs) {
			if (canHandleFeed(feedID)) {
				String tleFeed = feedID.substring(feedID.indexOf(DataComponent.SEPARATOR) + 1);//+1 so we do not include the separator
				SortedMap<Long, Map<String, String>> data = new TreeMap<Long, Map<String, String>>();
				
				for (long time = startTime; time < endTime; time += 1000) {
					double dataValue = 0.0;
					Map <String, String> dataItem = new HashMap<String, String> ();
					RenderingInfo ri = new RenderingInfo(Double.toString(dataValue), Color.ORANGE, " ", Color.ORANGE, true);
					dataItem.put(FeedProvider.NORMALIZED_RENDERING_INFO, ri.toString());
					dataItem.put(FeedProvider.NORMALIZED_TIME_KEY,  Long.toString(time));
					dataItem.put(FeedProvider.NORMALIZED_VALUE_KEY, Double.toString(dataValue));
					dataItem.put(FeedProvider.NORMALIZED_TELEMETRY_STATUS_CLASS_KEY, "1");
					data.put(Long.valueOf(time), dataItem);
				}
				
				if (!data.isEmpty()) {
					out.put(feedID, data);
				}
			}
		}
		return out;
	}

	
	private boolean canHandleFeed(String feedId) {
		return feedId.startsWith(DataComponent.PREFIX) && feedId.contains(DataComponent.SEPARATOR);
	}

	@Override
	public LOS getLOS() {
		// use the medium level of service if the data can be accessed relatively quickly (local storage)
		// this case is nominal as the 
		return LOS.medium;
	}

	@Override
	public Map<String, List<Map<String, String>>> getData(Set<String> feedIDs, TimeUnit timeUnit, long startTime, long endTime) {
		Map<String, List<Map<String, String>>> out = new HashMap<String, List<Map<String, String>>>();
		
		for (Entry<String, SortedMap<Long, Map<String, String>>> entry :
			getData(feedIDs, startTime, endTime, timeUnit).entrySet()) {
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			list.addAll(entry.getValue().values());

			out.put(entry.getKey(), list);
		}
		return out;
	}


	@Override
	public boolean isFullyWithinTimeSpan(String feedID, long startTime,
			TimeUnit timeUnit) {
		// since this is the only provider for this specific type of Data this can always return
		// true. If there were multiple data providers working together this would return false
		// if additional data sources should be consulted.
		return true;
	}
}
