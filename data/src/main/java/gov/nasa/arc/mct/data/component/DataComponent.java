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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.Bootstrap;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.FeedType;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.data.action.BundleAccess;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.activity.TimeService;

/**
 * Represents data corresponding to each dictionary import.
 * @author jdong2
 *
 */
public class DataComponent extends AbstractComponent implements FeedProvider {

	public static final String PREFIX = BundleAccess.BUNDLE.getString("data_component_prefix") + ":";
	public static final String SEPARATOR = ":";
	
	/**
	 * the DataTaxonomyComponent which stores the end time stamp 
	 * for the data feed of current component.
	 */
	private DataTaxonomyComponent parent;

	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (capability.isAssignableFrom(FeedProvider.class)) {
			return capability.cast(this);
		}
		return super.handleGetCapability(capability);
	}
	
	/**
	 * @returns an unique ID to look up the associated value
	 * stored in the disk.
	 */
	@Override
	public String getSubscriptionId() {
		return PREFIX + getExternalKey();
	}

	/**
	 * @return the end time stamp for the corresponding feed.
	 * getTimeService() should return the current time meaningful
	 * to the data source, so that when rendering, the time range 
	 * is appropriate to the feed.
	 */
	@Override
	public TimeService getTimeService() {
		DataTaxonomyComponent parentReference = getParent();
		String id = getSubscriptionId();
		
		// if corresponding data has been saved into database (using
		// Import > Data), use the saved end time stamp 
		if (parentReference.hasTimeStamp(id)) {
			final long endTime = parentReference.getTimeStamp(id);
			return new TimeService() {
				public long getCurrentTime() {
					return endTime;
				}
			};		
		}
		
		// else use current time of system
		return new TimeService() {
			@Override
			public long getCurrentTime() {
				return System.currentTimeMillis();
			}				
		};
	}

	/**
	 * cannot return parent directly due to the value may be null.
	 * Instead, use getReferencingComponents() to fetch references from database.
	 * 
	 * @return the DataTaxonomyComponent which acts as
	 *         parent in the tree structure
	 */
	public DataTaxonomyComponent getParent() {
		Collection<AbstractComponent> owners =  this.getReferencingComponents();
		for (AbstractComponent owner: owners) {
			if (owner instanceof DataTaxonomyComponent) {
				parent = (DataTaxonomyComponent)owner;
			}
		} 
		return parent; 		
	}

	public void setParent(DataTaxonomyComponent parent) {
		this.parent = parent;
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
		return getTimeService().getCurrentTime();
	}

	@Override
	public boolean isPrediction() {
		return false;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	/**
	 * Static data reading from database, so it is Non-change-of-data
	 */
	@Override
	public boolean isNonCODDataBuffer() {
		return true;
	}
}
