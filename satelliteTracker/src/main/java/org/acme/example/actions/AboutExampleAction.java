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
import gov.nasa.arc.mct.gui.ContextAwareAction;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import gov.nasa.arc.mct.gui.OptionBox;

/**
 * The <code>AboutExampleAction</code> demonstrates an about message action.  
 *
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public class AboutExampleAction extends ContextAwareAction {

	private static ResourceBundle bundle = ResourceBundle.getBundle("ExampleResourceBundle"); //NO18N
	
	public AboutExampleAction() {
		super(bundle.getString("about_text")); //NO18N
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        OptionBox.showMessageDialog((Component) e.getSource(), bundle.getString("about_message")); //NO18N
	}

	@Override
	public boolean canHandle(ActionContext context) {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
