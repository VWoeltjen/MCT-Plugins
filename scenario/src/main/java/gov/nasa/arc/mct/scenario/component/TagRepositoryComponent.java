package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

public class TagRepositoryComponent extends AbstractComponent implements RepositoryCapability {
	private TagRepositoryModel model;
	
	@Override
	public <T> T handleGetCapability(Class<T> capabilityClass) {
		return (capabilityClass.isAssignableFrom(ModelStatePersistence.class)) ?
			capabilityClass.cast(persistence) :
			super.handleGetCapability(capabilityClass);
	}
	
	@Override
	public Class<?> getCapabilityClass() {
		return TagComponent.class;
	}

	@Override
	public String getUserScope() {
		return model.scope;
	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class TagRepositoryModel {
		private String scope;
	}
	
	private final ModelStatePersistence persistence = 
			new JAXBModelStatePersistence<TagRepositoryModel>() {
		@Override
		protected TagRepositoryModel getStateToPersist() {
			return model;
		}

		@Override
		protected void setPersistentState(TagRepositoryModel modelState) {
			model = (TagRepositoryModel) modelState;
		}

		@Override
		protected Class<TagRepositoryModel> getJAXBClass() {
			return TagRepositoryModel.class;
		}		
	};
}
