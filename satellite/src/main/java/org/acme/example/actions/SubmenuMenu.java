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
package org.acme.example.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.acme.example.component.ExampleComponent;

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareMenu;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;

/**
 * This class implements a submenu that appears under the Objects menu.
 * 
 * @author nshi
 */
@SuppressWarnings("serial")
public class SubmenuMenu extends ContextAwareMenu {
	
    private static final String OBJECTS_SUBMENU_EXT = "objects/submenu.ext";

	public SubmenuMenu() {
		super("Example Submenu");
	}

	@Override
	protected void populate() {
        Collection<MenuItemInfo> infos = new ArrayList<MenuItemInfo>();
        infos.add(new MenuItemInfo("OBJECTS_SUBMENU_BEEP", MenuItemType.NORMAL));
        addMenuItemInfos(OBJECTS_SUBMENU_EXT, infos);
	}
	
	@Override
	public boolean canHandle(ActionContext context) {
		Collection<View> selectedManifestations = context.getSelectedManifestations();
		if (selectedManifestations.size() != 1)
			return false;
		
		return selectedManifestations.iterator().next().getManifestedComponent() instanceof ExampleComponent;
	}

}
