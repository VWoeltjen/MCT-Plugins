package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.chronology.timeline.policy.TimelineFilterViewPolicy;
import gov.nasa.arc.mct.chronology.timeline.policy.TimelinePreferredViewPolicy;
import gov.nasa.arc.mct.chronology.timeline.view.TimelineView;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.scenario.api.NewActivityAction;
import gov.nasa.arc.mct.scenario.policy.FilterViewPolicy;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

public class ActivityComponentProvider extends AbstractComponentProvider {

	// use a resource bundle for strings to enable localization in the future if required
	private static ResourceBundle bundle = ResourceBundle.getBundle("Bundle"); 
	private static final String OBJECTS_CREATE_EXT_PATH = "/objects/creation.ext";
	
	private static final ComponentTypeInfo activityComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_activity"),  
			bundle.getString("description_activity"), 
			ActivityComponent.class,
			new ActivityWizardUI());

	
	@Override
	public Collection<ComponentTypeInfo> getComponentTypes() {
		// return the component types provided
		System.out.println("Getting component types");
		return Arrays.asList(activityComponentType);
	}

	@Override
	public Collection<ViewInfo> getViews(String componentTypeId) {

		// return a view if desired for the components being created. Note that this method is called
		// for every component type so a view could be supplied for any component type not just
		// components supplied by this provider.
		
		// Also, note that the default node view, canvas view, and housing view will be supplied
		// by the MCT platform.
		if (componentTypeId.equals(ActivityComponent.class.getName())) {
			return Arrays.asList(
		    		new ViewInfo(TimelineView.class, TimelineView.VIEW_ROLE_NAME, ViewType.OBJECT),
		    		new ViewInfo(TimelineView.class, TimelineView.VIEW_ROLE_NAME, ViewType.EMBEDDED),
		    		new ViewInfo(TimelineView.class, TimelineView.VIEW_ROLE_NAME, ViewType.CENTER)
					
			);
		}
		return Collections.emptyList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<MenuItemInfo> getMenuItemInfos() {
		return Arrays.asList(				
				new MenuItemInfo(
								OBJECTS_CREATE_EXT_PATH, 
								"OBJECT_CREATE_ACTIVITY", 
								MenuItemType.NORMAL,
								NewActivityAction.class)

				// Additional menu items can be added to the following 
				// menus with the corresponding menubarPaths:
				// This    => /this/additions
				// Objects => /objects/additions
				// Help    => /help/additions
		);		
				// add menu items to help -- this will show up as a help topic for the example plugin
		
	}

	
	@Override
	public Collection<PolicyInfo> getPolicyInfos() {
		/*
		 * Here is an example of registering a policy to a platform provided
		 * policy category. Platform-provided policy categories are defined
		 * in PolicyInfo.CatetoryType, which is an enum.
		 * A new category can also be added by passing in a unique String to
		 * the category name in PolicyInfo constructor.
		 */
		return Arrays.asList(
        new PolicyInfo(PolicyInfo.CategoryType.FILTER_VIEW_ROLE.getKey(),
                TimelineFilterViewPolicy.class),
        new PolicyInfo(PolicyInfo.CategoryType.PREFERRED_VIEW.getKey(),
        		TimelinePreferredViewPolicy.class)
				);
		
		/* 
		 * External plugins can execute a policy category by accessing the
		 * PolicyManager, which is available as an OSGi service.
		 * 
		 * To access the PolicyManager, a class PolicyManagerAccess should be created.
		 * This class is used to inject an instance of the PolicyManager using declarative 
		 * services (see OSGI-INF/component.xml for examples for ComponentRegistryAccess).
		 *
		 * The following code snippet shows how to execute a policy category:
		 * 
		 *   PolicyManager policyManager = PolicyManagerAccess.getPolicyManager();
		 *   PolicyContext context = new PolicyContext();
		 *   context.setProperty(String key, Object value);
		 *   ...
		 *   ... maybe more properties to be set
		 *   ...
		 *   ExecutionResult result = 
		 *     policyManager.execute(String categoryKey, PolicyContext context); 
		 */
	}
}
