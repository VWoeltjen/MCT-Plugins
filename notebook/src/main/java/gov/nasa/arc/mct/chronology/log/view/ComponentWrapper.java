package gov.nasa.arc.mct.chronology.log.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.activity.TimeService;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComponentWrapper extends AbstractComponent {



	private AbstractComponent comp;
	private FeedProvider      feed = null;
	private TimeService       timeService;

	public ComponentWrapper(AbstractComponent c, final long time) {
		comp = c;
		FeedProvider fp = comp.getCapability(FeedProvider.class);
		timeService = new TimeService() {
			@Override
			public long getCurrentTime() {
				return time;
			}
		};
		if (fp != null) {
			feed = new FeedProviderWrapper(fp);
		}
	}
	
	@Override
	protected <T> T handleGetCapability (Class<T> capability) {
		if (capability.isAssignableFrom(FeedProvider.class)){
			return capability.cast(feed);
		}
		return comp.getCapability(capability);
	}
	
	// Everything else just delegates...
	public AbstractComponent getWorkUnitDelegate() {
		return comp.getWorkUnitDelegate();
	}

	public String getComponentTypeID() {
		return comp.getComponentTypeID();
	}

	public Set<ViewInfo> getViewInfos(ViewType type) {
		return comp.getViewInfos(type);
	}

	public String getId() {
		return comp.getId();
	}

	public String getComponentId() {
		return comp.getComponentId();
	}

	public void setId(String id) {
		comp.setId(id);
	}

	public void setOwner(String owner) {
		comp.setOwner(owner);
	}

	public String getOwner() {
		return comp.getOwner();
	}

	public String getCreator() {
		return comp.getCreator();
	}

	public Date getCreationDate() {
		return comp.getCreationDate();
	}

	public Collection<AbstractComponent> getReferencingComponents() {
		return comp.getReferencingComponents();
	}

	public String getDisplayName() {
		return comp.getDisplayName();
	}

	public String getExtendedDisplayName() {
		return comp.getExtendedDisplayName();
	}

	public String getExternalKey() {
		return comp.getExternalKey();
	}

	public void setExternalKey(String key) {
		comp.setExternalKey(key);
	}

	public void setDisplayName(String name) {
		comp.setDisplayName(name);
	}

	public void setAndUpdateDisplayName(String name) {
		comp.setAndUpdateDisplayName(name);
	}

	public void setAndUpdateOwner(String name) {
		comp.setAndUpdateOwner(name);
	}

	public void save() {
		comp.save();
	}

	public AbstractComponent clone() {
		return comp.clone();
	}

	public int getVersion() {
		return comp.getVersion();
	}

	public void addViewManifestation(View viewManifestation) {
		comp.addViewManifestation(viewManifestation);
	}

	public boolean equals(Object arg0) {
		return comp.equals(arg0);
	}

	public Set<View> getAllViewManifestations() {
		return comp.getAllViewManifestations();
	}

	public List<AbstractComponent> getComponents() {
		return comp.getComponents();
	}

	public List<PropertyDescriptor> getFieldDescriptors() {
		return comp.getFieldDescriptors();
	}

	public int hashCode() {
		return comp.hashCode();
	}

	public boolean isLeaf() {
		return comp.isLeaf();
	}

	public void removeDelegateComponents(
			Collection<AbstractComponent> childComponents) {
		comp.removeDelegateComponents(childComponents);
	}

	public boolean isDirty() {
		return comp.isDirty();
	}

	public boolean isStale() {
		return comp.isStale();
	}

	public void resetComponentProperties(ResetPropertiesTransaction txn) {
		comp.resetComponentProperties(txn);
	}

	public String toString() {
		return comp.toString();
	}	
	
	
	private class FeedProviderWrapper implements FeedProvider {
		private FeedProvider feed;
		
		public FeedProviderWrapper(FeedProvider fp) {
			feed = fp;
		}

		public TimeService getTimeService() {
			return timeService;
		}
		
		//Everything else just delegates...
		public String getSubscriptionId() {
			return feed.getSubscriptionId();
		}

		public String getLegendText() {
			return feed.getLegendText();
		}

		public int getMaximumSampleRate() {
			return feed.getMaximumSampleRate();
		}

		public FeedType getFeedType() {
			return feed.getFeedType();
		}

		public String getCanonicalName() {
			return feed.getCanonicalName();
		}

		public RenderingInfo getRenderingInfo(Map<String, String> data) {
			return feed.getRenderingInfo(data);
		}

		public long getValidDataExtent() {
			return feed.getValidDataExtent();
		}

		public boolean isPrediction() {
			return feed.isPrediction(); //TODO : True?
		}

		@Override
		public boolean isNonCODDataBuffer() {
			return feed.isNonCODDataBuffer();
		}
		
	}	
	
}
