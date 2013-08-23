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
package org.acme.example.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.acme.example.component.ExampleComponent;
import org.acme.example.component.ExampleModelRole;

/**
 * The <code>SaveModelStateViewRole</code> class provides a view exposed in the inspector area, which is
 * the right hand side of a window. This view demonstrates how to mutate, save, and respond to changes
 * in model data. 
 *
 */
@SuppressWarnings("serial")
public final class SaveModelStateView extends View {
	// use a resource bundle for strings to enable localization in the future if required
	private static ResourceBundle bundle = ResourceBundle.getBundle("ExampleResourceBundle");
    private JFormattedTextField doubleDataTextField;
    private JTextField descriptionTextField;
    
    // get this component and its associated model role
    private ExampleModelRole mr = ExampleComponent.class.cast(getManifestedComponent()).getModel(); 

    // create the GUI
    public SaveModelStateView(AbstractComponent ac, ViewInfo vi)  {
        super(ac,vi);
       
        // This GUI allows a user to modify the component's data and persist it.
        TitledBorder titledBorder = BorderFactory.createTitledBorder(bundle.getString("ModelBorderTitle"));
        final JPanel jp = new JPanel();
        jp.setBorder(titledBorder);
        descriptionTextField = new JTextField();
        descriptionTextField.setText(mr.getData().getDataDescription());
        descriptionTextField.setToolTipText(bundle.getString("DescriptionToolTip"));
        doubleDataTextField = new JFormattedTextField(NumberFormat.getInstance());
        doubleDataTextField.setValue(mr.getData().getDoubleData());
        doubleDataTextField.setToolTipText(bundle.getString("ValueToolTip"));
        // ensure the value is really a double before allowing the focus to change
        doubleDataTextField.setInputVerifier(new InputVerifier() {
			
			@Override
			public boolean verify(JComponent input) {
				JTextField inputField = (JTextField) input;
				try {
					Double.parseDouble(inputField.getText());
				} catch (NumberFormatException e) {
					return false;
				}
				return true;
			}
		});
        JButton saveButton = new JButton(bundle.getString("SaveButton"));

        jp.add(descriptionTextField);
        jp.add(doubleDataTextField);
        addToLayout(jp, bundle.getString("Description"), descriptionTextField, 
        				bundle.getString("Value"), doubleDataTextField, saveButton);
        saveButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {          	
                AbstractComponent component = getManifestedComponent();  
                
                // Update the model from the GUI input.
			    mr.getData().setDoubleData(Double.parseDouble(doubleDataTextField.getText()));
			    mr.getData().setDataDescription(descriptionTextField.getText());
      		
        		// Save the component
        		component.save();  
        	}
        });
       
        add(jp);
    }
    
    /**
     * Configure the grid bag layout that is not really relevant for demonstrating
     * MCT API usage. 
     **/
    private void addToLayout(Container c, String topLabelString, JTextField topField, 
    						String bottomLabelString, JTextField bottomField, JButton button) {
    	GridBagLayout gbl = new GridBagLayout();
		c.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 0, 0);
		
		JLabel topLabel = new JLabel(topLabelString);
		topLabel.setLabelFor(topField);
		JLabel bottomLabel = new JLabel(bottomLabelString);
		bottomLabel.setLabelFor(bottomField);
		c.add(topLabel, gbc);
		gbc.gridy = 1;
		c.add(bottomLabel, gbc);
		gbc.gridy = 2;
		gbc.gridx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.insets = new Insets(5, 5, 0, 5);
		c.add(button, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weighty = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 0, 0, 5);
		c.add(topField, gbc);
		gbc.gridy = 1;
		c.add(bottomField, gbc);
    }
    
    // This method is a callback from BaseComponent refreshViewManifestations().
	// Its job is to refresh all the GUI manifestations of this view role. In the case of updating the model,
    // the refresh is done by copying new data from the model to the GUI visual elements.
	// In our example, the visual elements are a text field and a double value field.
    @Override
    public void updateMonitoredGUI() {
    	ExampleModelRole mr =  ExampleComponent.class.cast(getManifestedComponent()).getModel();
    	doubleDataTextField.setValue(mr.getData().getDoubleData());
    	descriptionTextField.setText(mr.getData().getDataDescription());     	
    }
    
}