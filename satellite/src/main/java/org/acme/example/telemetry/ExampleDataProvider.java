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
package org.acme.example.telemetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import gov.nasa.arc.mct.api.feed.DataProvider;
import gov.nasa.arc.mct.gui.FeedView;

/**
 * This class demonstrates how to write a data adapter. This class will be used in the feed aggregator
 * to retrieve data when requested by <{@link FeedView}> instances. This class should be thread
 * safe as this may be accessed concurrently. 
 *
 */
public class ExampleDataProvider implements DataProvider {
	private Map<String, TestDataFeed> feeds = new ConcurrentHashMap<String, TestDataFeed>();


	@Override
	public Map<String, SortedMap<Long, Map<String, String>>> getData(Set<String> feedIDs, long startTime, long endTime, TimeUnit timeUnit) {
		Map<String, SortedMap<Long, Map<String, String>>> out = new HashMap<String, SortedMap<Long, Map<String, String>>>();
		for(String feedID : feedIDs) {
			if (canHandleFeed(feedID)) {
				TestDataFeed feed = feeds.get(feedID);
				if(feed == null) {
					feed = new TestDataFeed();
					feed.setPeriodInSeconds(feed.getPeriodInSeconds() * (1 + feed.hashCode() / (double) Integer.MAX_VALUE / 2));
					feed.setAmplitude(feed.getAmplitude() * (1 + (feed + " ").hashCode() / (double) Integer.MAX_VALUE / 2));
					feeds.put(feedID, feed);
				}
				out.put(feedID, feed.getData(startTime, endTime, timeUnit));
			}
		}
		return out;
	}

	
	private boolean canHandleFeed(String feedId) {
		return feedId.startsWith(TelemetryComponent.TelemetryPrefix);
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
		for(String feedID : feedIDs) {
			TestDataFeed feed = feeds.get(feedID);
			if(feed == null) {
				feed = new TestDataFeed();
				feeds.put(feedID, feed);
			}
			out.put(feedID, new ArrayList<Map<String, String>>(feed.getData(startTime, endTime, timeUnit).values()));
		}
		return out;
	}


	@Override
	public boolean isFullyWithinTimeSpan(String feedID, long startTime,
			TimeUnit timeUnit) {
		// since this is the only provider for this specific type of telemetry this can always return
		// true. If there were multiple data providers working together this would return false
		// if additional data sources should be consulted.
		return true;
	}
}
