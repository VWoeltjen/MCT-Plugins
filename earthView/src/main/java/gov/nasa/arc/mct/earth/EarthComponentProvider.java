package gov.nasa.arc.mct.earth;

import gov.nasa.arc.mct.earth.component.CoordinateComponent;
import gov.nasa.arc.mct.earth.component.UserOrbitalComponent;
import gov.nasa.arc.mct.earth.component.VectorComponent;
import gov.nasa.arc.mct.earth.component.wizard.OrbitalWizard;
import gov.nasa.arc.mct.earth.policy.GlobalViewPolicy;
import gov.nasa.arc.mct.earth.view.EarthView;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Arrays;
import java.util.Collection;

public class EarthComponentProvider extends AbstractComponentProvider {
	private static final Collection<ViewInfo> VIEWS = Arrays.asList(
			new ViewInfo(EarthView.class, "Global", ViewType.OBJECT),
			new ViewInfo(EarthView.class, "Global", ViewType.EMBEDDED)
	);
	
	private static final Collection<ComponentTypeInfo> COMPONENTS = Arrays.asList(
			//new ComponentTypeInfo("Orbital", "Part of an orbit", OrbitalComponent.class, true),
			new ComponentTypeInfo("Orbit",   "",                 UserOrbitalComponent.class, new OrbitalWizard()),
			new ComponentTypeInfo("Vector",  "",                 VectorComponent.class, false),
			new ComponentTypeInfo("Coordinate", "",              CoordinateComponent.class, false)
	);
	
	private static final Collection<PolicyInfo> POLICIES = Arrays.asList(
			new PolicyInfo(PolicyInfo.CategoryType.FILTER_VIEW_ROLE.getKey(), GlobalViewPolicy.class)
	);
	
	@Override
	public Collection<PolicyInfo> getPolicyInfos() {
        return POLICIES;
	}
	
	@Override
	public Collection<ViewInfo> getViews(String componentTypeId) {
		
		return VIEWS;
	}

	@Override
	public Collection<ComponentTypeInfo> getComponentTypes() {
		return COMPONENTS;
	}
}
