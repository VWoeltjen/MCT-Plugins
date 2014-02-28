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
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class ScenarioTaxonomy {
	// use a resource bundle for strings to enable localization in the future if required
	private static ResourceBundle bundle = ResourceBundle.getBundle("Bundle"); 

	private List<AbstractComponent> bootstraps = new ArrayList<AbstractComponent>();
	
	public ScenarioTaxonomy() {
		initializeScenarioTaxonomy();
	}
	
	public Collection<AbstractComponent> getBootstrapComponents() {
		return bootstraps;
	}
	
	private void initializeScenarioTaxonomy() {
		AbstractComponent mission = initialize(
				new MissionComponent(),
				bundle.getString("bdn_mission"),
				bundle.getString("mission_uuid"),
				bundle.getString("mission_owner"));
		
		ComponentRegistry registry = PlatformAccess.getPlatform().getComponentRegistry();
		
		String wild = "*";
		String tagRepoId = bundle.getString("prefix_tagrepo") + wild;
		String typeRepoId = bundle.getString("prefix_typerepo") + wild;
		String missionTemplatesId = bundle.getString("missiontemplates_uuid");
		
		// Tag and Type repos may already exist
		AbstractComponent tagRepo = registry.getComponent(tagRepoId);
		AbstractComponent typeRepo = registry.getComponent(typeRepoId);
		AbstractComponent missionTemplates = registry.getComponent(missionTemplatesId);

		List<AbstractComponent> toPersist = new ArrayList<AbstractComponent>();
		
		if (tagRepo == null) {
			toPersist.add(tagRepo = initialize(new TagRepositoryComponent(),
					bundle.getString("bdn_missiontags"),
					tagRepoId, 
					wild));					
		}
		
		if (typeRepo == null) {
			toPersist.add(typeRepo = initialize(new CostRepositoryComponent(),
					bundle.getString("bdn_missiontypes"),
					typeRepoId, 
					wild));					
		}
		
		
		if (missionTemplates == null) {
			toPersist.add(missionTemplates = initialize(
					new ScenarioTaxonomyComponent(),
					bundle.getString("bdn_missiontemplates"),
					missionTemplatesId,
					bundle.getString("mission_owner")));
		}
		
		missionTemplates.addDelegateComponents(Arrays.asList(typeRepo, tagRepo));

		// Workaround lack of support for introducing bootstrap components with children
		PlatformAccess.getPlatform().getPersistenceProvider().persist(toPersist);
		
		mission.addDelegateComponent(missionTemplates);
		
		bootstraps.add(mission);	
	}
	
	private AbstractComponent initialize(
			AbstractComponent ac, String bdn, String id, String owner) {
		ac.setDisplayName(bdn);
		ac.getCapability(ComponentInitializer.class).setId(id);		
		ac.getCapability(ComponentInitializer.class).setOwner(owner);
		ac.getCapability(ComponentInitializer.class).setCreator(owner);
		return ac;
	}
}
