package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;

import java.util.concurrent.atomic.AtomicReference;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

public class TagRepositoryComponent extends AbstractComponent implements RepositoryCapability {
	private AtomicReference<TagRepositoryModel> model =
			new AtomicReference<TagRepositoryModel>(new TagRepositoryModel());

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
		return model.get().scope;
	}
	
	public void setUserScope(String scope) {
		model.get().scope = scope;
	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class TagRepositoryModel {
		private String scope = "";
	}
	
	private final ModelStatePersistence persistence = 
			new JAXBModelStatePersistence<TagRepositoryModel>() {
		@Override
		protected TagRepositoryModel getStateToPersist() {
			return model.get();
		}

		@Override
		protected void setPersistentState(TagRepositoryModel modelState) {
			model.set(modelState);
		}

		@Override
		protected Class<TagRepositoryModel> getJAXBClass() {
			return TagRepositoryModel.class;
		}		
	};
}
