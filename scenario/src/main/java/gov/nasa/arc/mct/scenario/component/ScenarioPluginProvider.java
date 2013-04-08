package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.chronology.timeline.view.TimelineView;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;
import gov.nasa.arc.mct.scenario.api.NewActivityAction;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

public class ScenarioPluginProvider extends AbstractComponentProvider {

	// use a resource bundle for strings to enable localization in the future if required
	private static ResourceBundle bundle = ResourceBundle.getBundle("Bundle"); 
	private static final String OBJECTS_CREATE_EXT_PATH = "/objects/creation.ext";
	
	private static final ComponentTypeInfo activityComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_activity"),  
			bundle.getString("description_activity"), 
			ActivityComponent.class,
			new ActivityCreationWizardUI());

	private static final ComponentTypeInfo decisionComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_decision"),  
			bundle.getString("description_decision"), 
			DecisionComponent.class,
			new DecisionCreationWizardUI());

	
	@Override
	public Collection<ComponentTypeInfo> getComponentTypes() {
		// return the component types provided
		return Arrays.asList(activityComponentType, decisionComponentType);
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
		    		new ViewInfo(TimelineView.class, TimelineView.VIEW_ROLE_NAME, TimelineView.class.getName(), ViewType.EMBEDDED, null, null, true, ActivityComponent.class),
		    		new ViewInfo(TimelineView.class, TimelineView.VIEW_ROLE_NAME, TimelineView.class.getName(), ViewType.CENTER, null, null, true, ActivityComponent.class)
					
			);
		}
		return Collections.emptyList();
	}
	
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
}
