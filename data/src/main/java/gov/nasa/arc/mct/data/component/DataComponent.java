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

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.Bootstrap;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.FeedType;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.data.action.BundleAccess;
import gov.nasa.arc.mct.services.activity.TimeService;

/**
 * 
 * @author jdong2
 *
 */
public class DataComponent extends AbstractComponent implements FeedProvider {

	public static final String PREFIX = BundleAccess.BUNDLE.getString("data_component_prefix") + ":";
	public static final String SEPARATOR = ":";

	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (capability.isAssignableFrom(FeedProvider.class)) {
			return capability.cast(this);
		}
		return super.handleGetCapability(capability);
	}
		
	@Override
	public String getSubscriptionId() {
		return PREFIX + getExternalKey();
	}

	@Override
	public TimeService getTimeService() {
		return new TimeService() {
			@Override
			public long getCurrentTime() {
				return System.currentTimeMillis();
			}				
		};
	}

	@Override
	public String getLegendText() {			
		return getDisplayName();
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
		return getDisplayName();
	}

	@Override
	public RenderingInfo getRenderingInfo(Map<String, String> data) {
		String riAsString = data.get(FeedProvider.NORMALIZED_RENDERING_INFO);
		RenderingInfo ri = null;  
		ri = FeedProvider.RenderingInfo.valueOf(riAsString);   
		return ri;
	}

	@Override
	public long getValidDataExtent() {
		return System.currentTimeMillis();
	}

	@Override
	public boolean isPrediction() {
		return false;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public boolean isNonCODDataBuffer() {
		return false;
	}
}
