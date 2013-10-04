package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;

public class TagRepositoryComponent extends AbstractComponent implements RepositoryCapability {
	private String scope;
	
	@Override
	public Class<?> getCapabilityClass() {
		return TagComponent.class;
	}

	@Override
	public String getUserScope() {
		return scope;
	}
}
