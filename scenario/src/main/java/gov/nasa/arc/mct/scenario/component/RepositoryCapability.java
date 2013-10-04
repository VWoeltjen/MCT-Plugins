package gov.nasa.arc.mct.scenario.component;

public interface RepositoryCapability {
	// All components in a repository are expected to exhibit some specific capability
	public Class<?> getCapabilityClass();
	
	// Repositories may be specific to users, to groups, or global
	public String getUserScope();
}
