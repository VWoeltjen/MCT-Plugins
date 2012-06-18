package gov.nasa.arc.mct.labs.ancestorview.provider;

import gov.nasa.arc.mct.services.component.ComponentRegistry;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The <code>ComponentRegistryAccess</code> class is used to inject an instance of the <code>ComponentRegistry</code> using declarative 
 * services. This OSGi component does not expose an interface (see OSGI-INF/component.xml) and thus will be usable from other bundles (
 * the class is not exported from this bundle). This class is thread safe as this may be access 
 * from multiple threads and the registry instance must be visible across all threads. 
 *
 */
public class ComponentRegistryAccess {
	private static AtomicReference<ComponentRegistry> registry =
		new AtomicReference<ComponentRegistry>();

	// this is not a traditional singleton as this class is created by the OSGi declarative services mechanism. 
	
	/**
	 * Returns the component registry instance. This will not return null as the cardinality of 
	 * the component specified through the OSGi components services is 1. 
	 * @return a component registry service instance
	 */
	public static ComponentRegistry getComponentRegistry() {
		return registry.get();
	}
	
	/**
	 * set the active instance of the <code>ComponentRegistry</code>. This method is invoked by
	 * OSGi (see the OSGI-INF/component.xml file for additional details).
	 * @param componentRegistry available in MCT
	 */
	public void setRegistry(ComponentRegistry componentRegistry) {
		registry.set(componentRegistry);
	}
	
	/**
	 * release the active instance of the <code>ComponentRegistry</code>. This method is invoked by
	 * OSGi (see the OSGI-INF/component.xml file for additional details).
	 */
	public void releaseRegistry() {
		registry.set(null);
	}
}
