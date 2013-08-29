package gov.nasa.arc.mct.satellite.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.services.activity.TimeService;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CoordinateComponent extends AbstractComponent implements FeedProvider {
	
	private final String FEED_SEPERATOR = "/";
	
	private AtomicReference<CoordinateModel> model = new AtomicReference<CoordinateModel>();
	
	/*
	 * the time service for the satellite objects is just the current system time
	 */
	private static final TimeService TIME_SERVICE = new TimeService() {

		@Override
		public long getCurrentTime() {
			return System.currentTimeMillis();
		}
		
	};	
	
	public void setModel(CoordinateModel m) {
		model.set(m);
	}

	protected <T> T handleGetCapability(Class<T> capability) {
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
		    JAXBModelStatePersistence<CoordinateModel> persistence = new JAXBModelStatePersistence<CoordinateModel>() {

				@Override
				protected CoordinateModel getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(CoordinateModel modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<CoordinateModel> getJAXBClass() {
					return CoordinateModel.class;
				}
		        
			};
			
			return capability.cast(persistence);
		}
		
		if (capability.isAssignableFrom(FeedProvider.class)) {
			return capability.cast(this);
		}
		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see gov.nasa.arc.mct.components.FeedProvider#getSubscriptionId()
	 * the data provider will see this as a FeedID
	 */
	@Override
	public String getSubscriptionId() {
		return "SatOrbit:" + model.get().getTLE().getSatName() +FEED_SEPERATOR
		                + model.get().getTLE().getLine1() + FEED_SEPERATOR
		                + model.get().getTLE().getLine2() + FEED_SEPERATOR
		                + model.get().getParameterKey();
	}
	
	@Override
	public TimeService getTimeService() {
		return TIME_SERVICE;
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
		// TODO Auto-generated method stub
		return false;
	}

}
