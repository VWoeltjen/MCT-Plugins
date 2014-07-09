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
package gov.nasa.arc.mct.data.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.data.action.DataImportAction;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ComponentProvider for the Data plug-in. 
 */
public class DataComponentProvider extends AbstractComponentProvider {
	// use a resource bundle for strings to enable localization in the future if required
    private static ResourceBundle bundle = ResourceBundle.getBundle("Bundle"); 
    
	private List<AbstractComponent> bootstraps = new ArrayList<AbstractComponent>();
	
	private static final ComponentTypeInfo dataComponentType = new ComponentTypeInfo(
			bundle.getString("data_component_display_name"),  
			bundle.getString("data_component_description"), 
			DataComponent.class,
			false);
	
	public DataComponentProvider() {
		List<AbstractComponent> toPersist = new ArrayList<AbstractComponent>();
		bootstraps.add(initializeDataTaxonomy(toPersist));
		
		// Workaround lack of support for introducing bootstrap components with children
		PlatformAccess.getPlatform().getPersistenceProvider().persist(toPersist);
	}
	
	private AbstractComponent initializeDataTaxonomy(List<AbstractComponent> toPersist) {
		AbstractComponent dataComponent = initialize(
				new DataComponent(),
				bundle.getString("data_base_display_name"),
				bundle.getString("data_uuid"),
				bundle.getString("data_owner"));
		
		return dataComponent;
	}

	private AbstractComponent initialize(
			AbstractComponent ac, String bdn, String id, String owner) {
		ac.setDisplayName(bdn);
		
		ComponentInitializer dataComponentCapability = ac.getCapability(ComponentInitializer.class);
        dataComponentCapability.setId(id);
        dataComponentCapability.setOwner(owner);
        dataComponentCapability.setCreator(owner);
        
		return ac;
	}
	
	@Override
	public Collection<AbstractComponent> getBootstrapComponents() {
		return bootstraps;		
	}
	
	@Override
	public Collection<ComponentTypeInfo> getComponentTypes() {

		return Arrays.asList(dataComponentType);
	}

	@Override
	public Collection<MenuItemInfo> getMenuItemInfos() {
		return Arrays.asList(
				new MenuItemInfo("/objects/import.ext",
						"IMPORT_OBJECTS_CSV_ACTION", 
						MenuItemType.NORMAL, DataImportAction.class),
				new MenuItemInfo("/this/import.ext",
						"IMPORT_THIS_CSV_ACTION", 
						MenuItemType.NORMAL, DataImportAction.class));
	}
	
}
