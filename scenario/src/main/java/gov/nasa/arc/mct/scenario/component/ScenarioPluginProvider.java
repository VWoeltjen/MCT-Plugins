/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.CustomVisualControl;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.scenario.actions.ExportCSVAction.ObjectsExportCSVAction;
import gov.nasa.arc.mct.scenario.actions.ExportCSVAction.ThisExportCSVAction;
import gov.nasa.arc.mct.scenario.policy.RepositoryLinkPolicy;
import gov.nasa.arc.mct.scenario.policy.RepositoryRemovalPolicy;
import gov.nasa.arc.mct.scenario.policy.ScenarioContainmentPolicy;
import gov.nasa.arc.mct.scenario.policy.TaxonomyRemovalPolicy;
import gov.nasa.arc.mct.scenario.policy.TimelineFilterViewPolicy;
import gov.nasa.arc.mct.scenario.view.ScenarioView;
import gov.nasa.arc.mct.scenario.view.TimelineInspector;
import gov.nasa.arc.mct.scenario.view.TimelineView;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.services.component.CreateWizardUI;
import gov.nasa.arc.mct.services.component.TypeInfo;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * ComponentProvider for the scenario plug-in. Exposes Activities, Scenarios, Timelines, 
 * Decisions, and associated policies/views to the MCT platform.
 * 
 *
 */
public class ScenarioPluginProvider extends AbstractComponentProvider {

	// use a resource bundle for strings to enable localization in the future if required
	private static ResourceBundle bundle = ResourceBundle.getBundle("Bundle"); 
	
	private static final ComponentTypeInfo activityComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_activity"),  
			bundle.getString("description_activity"), 
			ActivityComponent.class,
			true);

	private static final ComponentTypeInfo decisionComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_decision"),  
			bundle.getString("description_decision"), 
			DecisionComponent.class,
			true);

	private static final ComponentTypeInfo timelineComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_timeline"),  
			bundle.getString("description_timeline"), 
			TimelineComponent.class,
			true);

	private static final ComponentTypeInfo scenarioComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_scenario"),  
			bundle.getString("description_scenario"), 
			ScenarioComponent.class,
			true);
	
	public static final ComponentTypeInfo tagComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_tag"),  
			bundle.getString("description_tag"), 
			TagComponent.class);
	
	private static final ComponentTypeInfo tagRepoComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_tagrepo"),  
			bundle.getString("description_tagrepo"), 
			TagRepositoryComponent.class,
			false);

	private static final ComponentTypeInfo costRepoComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_typerepo"),  
			bundle.getString("description_typerepo"), 
			CostRepositoryComponent.class,
			false);
	
	public static final ComponentTypeInfo activityTypeComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_activity_type"),  
			bundle.getString("description_activity_type"), 
			ActivityTypeComponent.class);

	private static final ComponentTypeInfo missionComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_mission"),  
			bundle.getString("description_mission"), 
			MissionComponent.class,
			false);

	private static final ComponentTypeInfo taxonomyComponentType = new ComponentTypeInfo(
			bundle.getString("display_name_taxonomy"),  
			bundle.getString("description_taxonomy"), 
			ScenarioTaxonomyComponent.class,
			false);
	
	private static final PolicyInfo timelineViewPolicy = new PolicyInfo(
			PolicyInfo.CategoryType.FILTER_VIEW_ROLE.getKey(), 
			TimelineFilterViewPolicy.class);
	
	private static final PolicyInfo containmentPolicy = new PolicyInfo(
			PolicyInfo.CategoryType.COMPOSITION_POLICY_CATEGORY.getKey(), 
			ScenarioContainmentPolicy.class);
	
	private static final PolicyInfo repositoryPolicy = new PolicyInfo(
			PolicyInfo.CategoryType.CAN_REMOVE_MANIFESTATION_CATEGORY.getKey(), 
			RepositoryRemovalPolicy.class);

	private static final PolicyInfo taxonomyPolicy = new PolicyInfo(
			PolicyInfo.CategoryType.CAN_REMOVE_MANIFESTATION_CATEGORY.getKey(), 
			TaxonomyRemovalPolicy.class);
	
	private static final PolicyInfo repositoryLinkPolicy = new PolicyInfo(
			PolicyInfo.CategoryType.COMPOSITION_POLICY_CATEGORY.getKey(), 
			RepositoryLinkPolicy.class);
	
	private ScenarioTaxonomy taxonomy = null;
	
	private Map<Class<?>, ImageIcon> iconMap = new HashMap<Class<?>, ImageIcon>();
	
	public ScenarioPluginProvider() {
		iconMap.put(ActivityComponent.class, 
				new ImageIcon(ScenarioPluginProvider.class.getResource("/icons/mct_icon_activity.png")));
		iconMap.put(DecisionComponent.class, 
				new ImageIcon(ScenarioPluginProvider.class.getResource("/icons/mct_icon_decision.png")));
		iconMap.put(TimelineComponent.class, 
				new ImageIcon(ScenarioPluginProvider.class.getResource("/icons/mct_icon_timeline.png")));
		iconMap.put(ScenarioComponent.class, 
				new ImageIcon(ScenarioPluginProvider.class.getResource("/icons/mct_icon_scenario.png")));
		iconMap.put(TagComponent.class, 
				new ImageIcon(ScenarioPluginProvider.class.getResource("/icons/mct_icon_tag.png")));

	}
	
	@Override
	public Collection<ComponentTypeInfo> getComponentTypes() {
		// return the component types provided
		return Arrays.asList(
				activityComponentType, 
				timelineComponentType, 
				decisionComponentType, 
				scenarioComponentType,
				missionComponentType,
				taxonomyComponentType,
				activityTypeComponentType,
				tagComponentType,
				tagRepoComponentType,
				costRepoComponentType
				);
	}

	@Override
	public Collection<ViewInfo> getViews(String componentTypeId) {

		// return a view if desired for the components being created. Note that this method is called
		// for every component type so a view could be supplied for any component type not just
		// components supplied by this provider.
		

		
		// TimelineFilterViewPolicy will suppress these as appropriate
		return Arrays.asList(
			    	new ViewInfo(ScenarioView.class, "Scenario", ViewType.CENTER),
			    	new ViewInfo(ScenarioView.class, "Scenario", ViewType.OBJECT),
			    	/*new ViewInfo(SummaryView.class, "Summary", ViewType.CENTER),
			    	new ViewInfo(SummaryView.class, "Summary", ViewType.OBJECT),*/ 		    	
				    new ViewInfo(TimelineView.class, "Timeline", ViewType.CENTER),
				    new ViewInfo(TimelineInspector.class, "Timeline Inspector", ViewType.CENTER_OWNED_INSPECTOR),
					new ViewInfo(TimelineView.class, "Timeline", ViewType.OBJECT));
	}

	@Override
	public Collection<PolicyInfo> getPolicyInfos() {
		return Arrays.asList(
				timelineViewPolicy, 
				containmentPolicy, 
				taxonomyPolicy,
				repositoryLinkPolicy,
				repositoryPolicy
				);
	}

	@Override
	public Collection<AbstractComponent> getBootstrapComponents() {
		return getTaxonomy().getBootstrapComponents();
	}

	private ScenarioTaxonomy getTaxonomy() {
		return (taxonomy = (taxonomy == null ? new ScenarioTaxonomy() : taxonomy));
	}
	
	@Override
	public <T> T getAsset(TypeInfo<?> type, Class<T> assetClass) {
		// Create wizards
		if (assetClass.isAssignableFrom(CreateWizardUI.class)) {
			if (ActivityComponent.class.isAssignableFrom(type.getTypeClass())) {
				return assetClass.cast(new ActivityCreationWizardUI(getRepositoryMap()));
			}
			if (DecisionComponent.class.isAssignableFrom(type.getTypeClass())) {
				return assetClass.cast(new DecisionCreationWizardUI());
			}
			if (ActivityTypeComponent.class.isAssignableFrom(type.getTypeClass())) {
				String user = PlatformAccess.getPlatform().getCurrentUser().getUserId();
				return assetClass.cast(new RepositoryWizardDecorator(
						new ActivityTypeCreationWizardUI(),
						bundle.getString("prefix_typerepo") + user
						));
			}
			if (TagComponent.class.isAssignableFrom(type.getTypeClass())) {
				String user = PlatformAccess.getPlatform().getCurrentUser().getUserId();
				return assetClass.cast(new RepositoryWizardDecorator(
						new TagCreationWizardUI(),
						bundle.getString("prefix_tagrepo") + user
						));
			}
		}
		
		// Icons
		if (assetClass.isAssignableFrom(ImageIcon.class)) {
			Class<?> typeClass = type.getTypeClass();
			if (iconMap.containsKey(typeClass)) {
				return assetClass.cast(iconMap.get(typeClass));
			}
		}
	
		// Custom editors
		if (assetClass.isAssignableFrom(CustomVisualControl.class)) {
			if (ActivityComponent.class.isAssignableFrom(type.getTypeClass())) {				
				return assetClass.cast(new CompositeActivityVisualControl(getRepositoryMap()));
			} else if (ActivityTypeComponent.class.isAssignableFrom(type.getTypeClass())) {
				return assetClass.cast(new LinkVisualControl());
			}
		}
		
		// Default behavior
		return super.getAsset(type, assetClass);
	}

	private Map<ComponentTypeInfo, List<AbstractComponent>> getRepositoryMap() {
		String user = PlatformAccess.getPlatform().getCurrentUser().getUserId();
		String wild = "*";
		ComponentRegistry registry = PlatformAccess.getPlatform().getComponentRegistry();
		
		Map<ComponentTypeInfo, List<AbstractComponent>> types = 
				new HashMap<ComponentTypeInfo, List<AbstractComponent>>();
		types.put(tagComponentType, Arrays.asList(
				registry.getComponent(bundle.getString("prefix_tagrepo") + wild),
				registry.getComponent(bundle.getString("prefix_tagrepo") + user)
				));
		types.put(activityTypeComponentType, Arrays.asList(
				registry.getComponent(bundle.getString("prefix_typerepo") + wild),
				registry.getComponent(bundle.getString("prefix_typerepo") + user)
				));
		
		return types;
	}

	@Override
	public Collection<MenuItemInfo> getMenuItemInfos() {
		return Arrays.asList(
				new MenuItemInfo("/objects/export.ext",
						"EXPORT_OBJECTS_CSV_ACTION", 
						MenuItemType.NORMAL, ObjectsExportCSVAction.class),
				new MenuItemInfo("/this/export.ext",
						"EXPORT_THIS_CSV_ACTION", 
						MenuItemType.NORMAL, ThisExportCSVAction.class));
	}
}
