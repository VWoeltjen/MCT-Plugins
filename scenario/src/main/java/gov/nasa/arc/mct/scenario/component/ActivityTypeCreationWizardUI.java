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
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.component.CreateWizardUI;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ActivityTypeCreationWizardUI extends CreateWizardUI {
	private JTextField name  = new JTextField();
	private JTextField comms = new JTextField();
	private JTextField power = new JTextField();
	private JButton createButton;
	
	public ActivityTypeCreationWizardUI() {
		comms.getDocument().addDocumentListener(documentListener);
		power.getDocument().addDocumentListener(documentListener);
	}
	
	@Override
	public JComponent getUI(JButton create) {
		this.createButton = create;
		
		JLabel nameLabel = new JLabel("Name: ");
		JLabel powerLabel = new JLabel("Power: ");
		JLabel commsLabel = new JLabel("Comms: ");
		
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
		// TODO Auto-generated method stub
		return null;
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
