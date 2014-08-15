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
public class TimelineCreationWizardUI extends CreateWizardUI {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("Bundle");
    	
	private JTextField name  = new JTextField(BUNDLE.getString("wizard_timeline_name_default"));
	private JTextField battery = new JTextField(BUNDLE.getString("wizard_timeline_battery_default"));
	private JTextField capacity = new JTextField(BUNDLE.getString("wizard_timeline_battery_capacity_default"));
	private JButton createButton;
	
	public TimelineCreationWizardUI() {
		capacity.getDocument().addDocumentListener(documentListener);
		battery.getDocument().addDocumentListener(documentListener);
	}
	
	@Override
	public JComponent getUI(JButton create) {
		this.createButton = create;
		
		JLabel nameLabel = new JLabel(BUNDLE.getString("wizard_timeline_name_label"));
		JLabel batteryLabel = new JLabel(BUNDLE.getString("wizard_timeline_battery_label"));
		JLabel capacityLabel = new JLabel(BUNDLE.getString("wizard_timeline_battery_capacity_label"));
		
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(panel);		
		groupLayout.setAutoCreateGaps(true);
			
		// Set up grid of labels, fields
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
			.addGroup(groupLayout.createParallelGroup()
				.addComponent(nameLabel)
				.addComponent(batteryLabel)
				.addComponent(capacityLabel)
			).addGroup(groupLayout.createParallelGroup()
				.addComponent(name)
				.addComponent(battery)
				.addComponent(capacity)
			)
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
			.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(nameLabel)
				.addComponent(name)
			).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(batteryLabel)
				.addComponent(battery)
			).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
				.addComponent(capacityLabel)
				.addComponent(capacity)
			)
		);
		panel.setLayout(groupLayout);
		updateCreateButton(); // Ensure initial validation state is consistent
		return panel;
	}

	@Override
	public AbstractComponent createComp(ComponentRegistry comp,
			AbstractComponent parentComp) {	
		double commValue = Double.parseDouble(capacity.getText());
		double powerValue = Double.parseDouble(battery.getText());
		ActivityTypeComponent activityType = 
				comp.newInstance(ActivityTypeComponent.class, parentComp); 
		activityType.addCost(CostType.COMM, commValue);
		activityType.addCost(CostType.POWER, powerValue);
		
		/** if need to calculate impedance value
		double impedanceValue = STANDARD_VOLTAGE * STANDARD_VOLTAGE / powerValue;
		activityType.addCost(CostType.IMPEDANCE, impedanceValue); */
					
		activityType.setDisplayName(name.getText());
		
		// Previous validation should ensure that no NFE is thrown
		/** activityType.setCosts(
				Double.parseDouble(power.getText()), 
				Double.parseDouble(comms.getText())); */
		
		return activityType;
	}
	
	private void updateCreateButton() {
		if (createButton != null) {
			try {
				Double.parseDouble(capacity.getText());
				Double.parseDouble(battery.getText());
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
