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

import gov.nasa.arc.mct.gui.CustomVisualControl;
import gov.nasa.arc.mct.util.MCTIcons;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class ActivityTypeVisualControl extends CustomVisualControl {
	private static final long serialVersionUID = 3538827305917390749L;	
	private JLabel label = new JLabel();
	private JTextField field = new JTextField();
	private JLabel icon = new JLabel(MCTIcons.generateIcon(0, 12, Color.GRAY));
	private boolean mutable = false;
	
	public ActivityTypeVisualControl() {
		setLayout(new BorderLayout());
		add(icon, BorderLayout.LINE_END);
		add(label, BorderLayout.CENTER);
	}
	
	@Override
	public void setValue(Object value) {
		field.setText(value.toString());
		label.setText(value.toString());
	}

	@Override
	public Object getValue() {
		return mutable ? field.getText() : label.getText();
	}

	@Override
	public void setMutable(boolean mutable) {
		remove(mutable ? field : label);
		this.mutable = mutable;
		add(mutable ? field : label, BorderLayout.CENTER);		
	}
}
