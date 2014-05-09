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

public class GeneratorFeedProvider implements FeedProvider {
	private String expression;
	
	public GeneratorFeedProvider(String expression) {
		super();
		this.expression = expression;
	}

	@Override
	public String getSubscriptionId() {
		return GeneratorEventProvider.GENERATOR_FEED_PREFIX + expression;
	}

	@Override
	public TimeService getTimeService() {
		return GeneratorEventProvider.TIME_SERVICE;
	}

	@Override
	public String getLegendText() {
		return expression;
	}

	@Override
	public int getMaximumSampleRate() {
		return 1;
	}

	@Override
	public FeedType getFeedType() {
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
		return false;
	}

	@Override
	public boolean isNonCODDataBuffer() {
		return true;
	}

}
