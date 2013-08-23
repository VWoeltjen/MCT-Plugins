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

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.GroupAction;
import gov.nasa.arc.mct.gui.View;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.acme.example.component.ExampleComponent;

/**
 * The <code>AddOrRemoveNodeBorderAction</code> demonstrates how to add a set of radio buttons
 * actions to the menu. 
 *
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public class AddOrRemoveNodeBorderAction extends GroupAction {
	private static ResourceBundle bundle = ResourceBundle.getBundle("ExampleResourceBundle"); 
	
	public AddOrRemoveNodeBorderAction() {
		this(bundle.getString("AddOrRemoveNodeBorderText"));
	}

	protected AddOrRemoveNodeBorderAction(String name) {
		super(name);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public boolean canHandle(ActionContext context) {
		Collection<View> selectedManifestations = context.getSelectedManifestations();
		
		if (selectedManifestations.size() != 1)
			return false;
		
		View manifestation = selectedManifestations.iterator().next();
		
		if (!(manifestation.getManifestedComponent() instanceof ExampleComponent))
			return false;
		
		RadioAction[] radioActions = {
			new AddLineBorder(manifestation),
			new RemoveLineBorder(manifestation)
		};
		setActions(radioActions);
		
		return true;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
	
	private class AddLineBorder extends RadioAction {

		private View manifestation;
		
		public AddLineBorder(View manifestation) {
			this.manifestation = manifestation;
			putValue(Action.NAME, "Turn On Border");
			putValue(Action.SHORT_DESCRIPTION, "The border will not be persisted for the next MCT session.");
		}
		
		@Override
		public boolean isMixed() {
			return false;
		}
		
		@Override
		public boolean isSelected() {
			Border border = manifestation.getBorder();
			return border instanceof LineBorder;
				
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			manifestation.setBorder(BorderFactory.createLineBorder(Color.red, 3));
		}
		
	}	

	private class RemoveLineBorder extends RadioAction {

		private View manifestation;
		
		public RemoveLineBorder(View manifestation) {
			this.manifestation = manifestation;
			putValue(Action.NAME, "Turn Off Border");
		}
		
		@Override
		public boolean isMixed() {
			return false;
		}
		
		@Override
		public boolean isSelected() {
			Border border = manifestation.getBorder();
			return border == null;
				
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			manifestation.setBorder(null);
		}
		
	}	

}
