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
package gov.nasa.arc.mct.generator;

import java.util.Map;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.services.activity.TimeService;

/**
 * Describes the data feed associated with a given Generator 
 * user object. This is exposed as a capability of 
 * GeneratorComponent.
 * 
 * @author vwoeltje
 */
public class GeneratorFeedProvider implements FeedProvider {
	/**
	 * The mathematical expression used to generate data; the 
	 * variable "t" in this expression indicates the current 
	 * time, in seconds since the UNIX epoch. 
	 */
	private String expression;
	
	/**
	 * Describe a data feed corresponding with the provided 
	 * expression.
	 * @param expression the expression used to generate data for this feed
	 */
	public GeneratorFeedProvider(String expression) {
		super();
		this.expression = expression;
	}

	@Override
	public String getSubscriptionId() {
		// Used to identify data associated with this feed
		// in data structures passed around by MCT.
		return GeneratorEventProvider.GENERATOR_FEED_PREFIX + expression;
	}

	@Override
	public TimeService getTimeService() {
		// Use the TimeService exposed by GeneratorEventProvider,
		// which reports the latest time stamp for which generated 
		// data is available.
		return GeneratorEventProvider.TIME_SERVICE;
	}

	@Override
	public String getLegendText() {
		// To be shown on plot legends.
		return expression;
	}

	@Override
	public int getMaximumSampleRate() {
		// Generator generates data once per second.
		return 1;
	}

	@Override
	public FeedType getFeedType() {
		// Data values are expressed as doubles.
		return FeedType.FLOATING_POINT;
	}

	@Override
	public String getCanonicalName() {
		return expression;
	}

	@Override
	public RenderingInfo getRenderingInfo(Map<String, String> data) {
		return RenderingInfo.valueOf(data.get(NORMALIZED_RENDERING_INFO));
	}

	@Override
	public long getValidDataExtent() {
		return getTimeService().getCurrentTime();
	}

	@Override
	public boolean isPrediction() {
		// Not predictive: In principle data values could be computed
		// for future times, but we do not do this.
		return false;
	}

	@Override
	public boolean isNonCODDataBuffer() {
		// We are not change-only data; new data points 
		// are generated even if data value is unchanged.
		return true;
	}

}
