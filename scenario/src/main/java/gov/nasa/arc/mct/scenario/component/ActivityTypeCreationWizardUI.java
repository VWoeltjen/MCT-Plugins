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

import java.util.ResourceBundle;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.scenario.util.CostType;
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.component.CreateWizardUI;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The wizard used when creating new Activity Type instances.
 */
public class ActivityTypeCreationWizardUI extends CreateWizardUI {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("Bundle");
    	
	private JTextField name  = new JTextField(BUNDLE.getString("wizard_activity_type_name_default"));
	private JTextField power = new JTextField(BUNDLE.getString("wizard_activity_type_power_default"));
	private JTextField comms = new JTextField(BUNDLE.getString("wizard_activity_type_comms_default"));
	private JButton createButton;
	public final static double STANDARD_VOLTAGE = 28.0;
	
	public ActivityTypeCreationWizardUI() {
		comms.getDocument().addDocumentListener(documentListener);
		power.getDocument().addDocumentListener(documentListener);
	}
	
	@Override
	public JComponent getUI(JButton create) {
		this.createButton = create;
		
		JLabel nameLabel = new JLabel(BUNDLE.getString("wizard_activity_type_name_label"));
		JLabel powerLabel = new JLabel(BUNDLE.getString("wizard_activity_type_power_label"));
		JLabel commsLabel = new JLabel(BUNDLE.getString("wizard_activity_type_comms_label"));
		
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(panel);		
		groupLayout.setAutoCreateGaps(true);
			
		// Set up grid of labels, fields
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
			.addGroup(groupLayout.createParallelGroup()
				.addComponent(nameLabel)
				.addComponent(powerLabel)
				.addComponent(commsLabel)
			).addGroup(groupLayout.createParallelGroup()
				.addComponent(name)
				.addComponent(power)
				.addComponent(comms)
			)
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
			.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(nameLabel)
				.addComponent(name)
			).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(powerLabel)
				.addComponent(power)
			).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(commsLabel)
				.addComponent(comms)
			)
		);
		panel.setLayout(groupLayout);
		updateCreateButton(); // Ensure initial validation state is consistent
		return panel;
	}

	@Override
	public AbstractComponent createComp(ComponentRegistry comp,
			AbstractComponent parentComp) {	
		double commValue = Double.parseDouble(comms.getText());
		double powerValue = Double.parseDouble(power.getText());
		ActivityTypeComponent activityType = 
				comp.newInstance(ActivityTypeComponent.class, parentComp); 
		activityType.addCost(CostType.COMM, commValue);
		activityType.addCost(CostType.POWER, powerValue);
		
		/** if need to calculate impedance value
		double impedanceValue = STANDARD_VOLTAGE * STANDARD_VOLTAGE / powerValue;
		activityType.addCost(CostType.IMPEDANCE, impedanceValue); */
					
		activityType.setDisplayName(name.getText());		
		return activityType;
	}
	
	private void updateCreateButton() {
		if (createButton != null) {
			try {
				Double.parseDouble(comms.getText());
				Double.parseDouble(power.getText());
				createButton.setEnabled(true);
			} catch (NumberFormatException nfe) {
				createButton.setEnabled(false);
			}
		}
	}
	
	private DocumentListener documentListener = new DocumentListener() {
		@Override
		public void changedUpdate(DocumentEvent evt) {
			updateCreateButton();
		}

		@Override
		public void insertUpdate(DocumentEvent evt) {
			updateCreateButton();
		}

		@Override
		public void removeUpdate(DocumentEvent evt) {
			updateCreateButton();
		}
	};
}
