package gov.nasa.arc.mct.chronology.notebook;

import gov.nasa.arc.mct.chronology.log.component.ComponentRegistryAccess;
import gov.nasa.arc.mct.services.component.ComponentRegistry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class NotebookActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) {
        ServiceReference sr = context.getServiceReference(ComponentRegistry.class.getName());
        Object o = context.getService(sr);
        context.ungetService(sr);
        
        assert o != null;
        
        (new ComponentRegistryAccess()).setRegistry((ComponentRegistry)o);
        //NO. ProcedureTaxonomyInjector.injectProcedures(ComponentRegistryAccess.getComponentRegistry());
    }

    @Override
    public void stop(BundleContext context) {
        (new ComponentRegistryAccess()).releaseRegistry(ComponentRegistryAccess.getComponentRegistry());
    }

}
