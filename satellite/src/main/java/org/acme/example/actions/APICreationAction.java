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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ComponentRegistry;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import org.acme.example.component.ComponentRegistryAccess;
import org.acme.example.component.ExampleComponent;

/**
 * The <code>ProgrammaticCreateInstance</code> demonstrates how to create a new component instance 
 * programmatically to all selected manifestations (assuming all of the manifestations are ExampleComponents). This action 
 * will in the objects menu and will affect the children, so this requires that if there are no selections in the window the
 * object owning the window will be selected. 
 * All the manifestations if they are shared must be unlocked.
 *
 * @author chris.webster@nasa.gov
 */
@SuppressWarnings("serial")
public class APICreationAction extends ContextAwareAction {
	private static final AtomicInteger newCount = new AtomicInteger();
	private static ResourceBundle bundle = ResourceBundle.getBundle("ExampleResourceBundle"); //NO18N
	
	/**
	 * The currently selected manifestations. This will be either the actually selected manifestations or the 
	 * component for the window if nothing is selected.
	 */
	private Collection<View> selectedManifestations;
	
	public APICreationAction() {
		super(bundle.getString("ProgrammaticCreate")); //NO18N
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// add a new example component to each component currently selected 
		for (View manifestation : selectedManifestations) {
			AbstractComponent component = manifestation.getManifestedComponent();
			createChildInstance(component);
		}
	}
	
	/**
	 * Adds a new instance of an ExampleComponent as a child instance of the component using the 
	 * <code>ComponentRegistry</code> APIs. 
	 * @param component to add child example component to. This component will be editable as this 
	 * has already been checked in the isEnable method. 
	 */
	private void createChildInstance(AbstractComponent component) {
		// acquire the ComponentRegistry through the access object. The ComponentRegistry
		// will be injected through the OSGi declarative services model
		ComponentRegistry registry = ComponentRegistryAccess.getComponentRegistry();
		
		if (registry != null) {
			// use the ComponentRegistry service to create a new instance of a component
			ExampleComponent ec = registry.newInstance(ExampleComponent.class, component);
		
			// additional information can be collected here to populate a model. This simply
			// sets a unique name 
			ec.setDisplayName("newComponentFromMenu"+newCount.incrementAndGet());
			
			// show the newly created component in it's own window
			ec.open();
		}
	}

	/**
	 * Determines if the component is an instance of ExampleComponent.
	 * @param component to match against the ExampleComponent type
	 * @return true if an ExampleComponent is provided false otherwise
	 */
	private boolean isExampleComponent(AbstractComponent component) {
		return component instanceof ExampleComponent;
	}
	
	@Override
	public boolean canHandle(ActionContext context) {
		// set the set of manifestations to be either the selected manifestations or the manifestation of the window
		selectedManifestations = context.getSelectedManifestations();
		if (selectedManifestations.isEmpty()) {
			selectedManifestations = Collections.singletonList(context.getWindowManifestation());
		}
		
		// only show the action if all the selected components are Example Components
		for (View manifestation : selectedManifestations) {
			AbstractComponent component = manifestation.getManifestedComponent();
			
			if (!(isExampleComponent(component))) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
