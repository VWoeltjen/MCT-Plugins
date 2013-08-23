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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.services.activity.TimeService;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The <code>TelemetryComponent</code> class defines a simple component
 * representing a single piece of telemetry. In this case,
 * <code>BaseComponent</code> provides all the functionality required (the
 * default implementation assumes this component can contain an arbitrary
 * component). The no argument constructor is required by the MCT platform. This
 * component will participate in object sharing and persistence like other MCT
 * components.
 * 
 */
public class TelemetryComponent extends AbstractComponent implements
		FeedProvider {
	
	public static final String TelemetryPrefix = "example:";
	private AtomicReference<TelemetryModel> model = new AtomicReference<TelemetryModel> (new TelemetryModel());
	
	public TelemetryComponent() {
		super();
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (FeedProvider.class.isAssignableFrom(capability)) {
			return capability.cast(this);
		}
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
		    JAXBModelStatePersistence<TelemetryModel> persistence = new JAXBModelStatePersistence<TelemetryModel>() {

				@Override
				protected TelemetryModel getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(TelemetryModel modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<TelemetryModel> getJAXBClass() {
					return TelemetryModel.class;
				}
		        
			};
			
			return capability.cast(persistence);
		}
		return null;
	}

	@Override
	public String getLegendText() {
		return getDisplayName() + "\n" + getExternalKey();
	}
	
	@Override
	public int getMaximumSampleRate() {
		// This should be based on metadata
		return 1;
	}

	@Override
	public String getSubscriptionId() {
		return TelemetryPrefix+getComponentId();
	}
	
	public TelemetryModel getModel() {
		return model.get();
	}

	@Override
	public TimeService getTimeService() {
		return TimeServiceImpl.getInstance();
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
		assert data.get(FeedProvider.NORMALIZED_VALUE_KEY) != null : "The VALUE key is required for a valid status.";
		assert data.get(FeedProvider.NORMALIZED_TIME_KEY) != null : "The TIME key is required for a valid status.";
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
	public boolean isNonCODDataBuffer() {
		// TODO Auto-generated method stub
		return false;
	}
}
