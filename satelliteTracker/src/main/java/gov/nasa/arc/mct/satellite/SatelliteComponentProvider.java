package gov.nasa.arc.mct.satellite;

import gov.nasa.arc.mct.satellite.component.CoordinateComponent;
import gov.nasa.arc.mct.satellite.component.SatelliteComponent;
import gov.nasa.arc.mct.satellite.component.VectorComponent;
import gov.nasa.arc.mct.satellite.wizard.SatelliteWizard;
import gov.nasa.arc.mct.earth.component.wizard.OrbitalWizard;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Arrays;
import java.util.Collection;


public class SatelliteComponentProvider extends AbstractComponentProvider {

	// this is the component type we are providing. The display name and description are used
	// by the MCT menu system for creating new instances and thus should be human readable
	// and descriptive
	private static final Collection<ComponentTypeInfo> COMPONENTS = Arrays.asList(
			new ComponentTypeInfo("Satellite",   "An object to track a satellite's movement. ",        SatelliteComponent.class, new SatelliteWizard()),
			new ComponentTypeInfo("Vector",  "",                 VectorComponent.class, false),
			new ComponentTypeInfo("Coordinate", "",              CoordinateComponent.class, false)
	);
	
	@Override
	public Collection<ComponentTypeInfo> getComponentTypes() {
		return COMPONENTS;
	}

}
