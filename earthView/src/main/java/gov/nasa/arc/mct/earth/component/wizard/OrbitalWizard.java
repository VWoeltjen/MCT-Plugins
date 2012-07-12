package gov.nasa.arc.mct.earth.component.wizard;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.earth.Vector;
import gov.nasa.arc.mct.earth.component.CoordinateComponent;
import gov.nasa.arc.mct.earth.component.CoordinateModel;
import gov.nasa.arc.mct.earth.component.UserOrbitalComponent;
import gov.nasa.arc.mct.earth.component.VectorComponent;
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.component.CreateWizardUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OrbitalWizard extends CreateWizardUI {
	private static final String[] DEFAULTS = { "5013", "2270", "-3970", "-5.1", "4.1", "-3.9" };
	private JTextField[] boxes = new JTextField[6];
	private JTextField   name  = new JTextField();
	
	@Override
	public JComponent getUI(final JButton create) {
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		name.setText("untitled orbit");
		for (int i = 0; i < 6; i++) {
			boxes[i] = new JTextField(DEFAULTS[i]);
		}
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 4;
		gbc.ipady = 2;
		gbc.insets = new Insets(2,8,2,8);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		
		panel.add(new JLabel("Name:"), gbc);
		//panel.add(new JLabel(""));
		gbc.gridx++;
		gbc.gridwidth = 2;
		panel.add(name, gbc);
		gbc.gridwidth = 1;
		
		gbc.gridx = 0;
		gbc.gridy++;
		panel.add(new JLabel(""), gbc);
		gbc.gridx++;
		panel.add(new JLabel("Position"), gbc);
		gbc.gridx++;
		panel.add(new JLabel("Velocity"), gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		panel.add(new JLabel("X"), gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		panel.add(boxes[0], gbc);
		gbc.gridx++;
		panel.add(boxes[3], gbc);
		gbc.fill = GridBagConstraints.NONE;
		
		gbc.gridx = 0;
		gbc.gridy++;
		panel.add(new JLabel("Y"), gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		panel.add(boxes[1], gbc);
		gbc.gridx++;
		panel.add(boxes[4], gbc);
		gbc.fill = GridBagConstraints.NONE;
		
		gbc.gridx = 0;
		gbc.gridy++;
		panel.add(new JLabel("Z"), gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		panel.add(boxes[2], gbc);
		gbc.gridx++;
		panel.add(boxes[5], gbc);
		gbc.fill = GridBagConstraints.NONE;
			
		return panel;
		
	}
	
	private double value(int i) {
		try {
			return Double.parseDouble(boxes[i].getText());
		} catch (Exception e) {
			return 0.0; // TODO : log ?
		}
	}

	@Override
	public AbstractComponent createComp(ComponentRegistry registry,
			AbstractComponent parentComp) {
	
		UserOrbitalComponent orbitalComponent = registry.newInstance(UserOrbitalComponent.class, parentComp);
		orbitalComponent.setDisplayName(name.getText());
		orbitalComponent.setOrbitalParameters(new Vector(value(0), value(1), value(2)), new Vector(value(3), value(4),  value(5)), System.currentTimeMillis());
		orbitalComponent.save();

		
		boolean[] truths   = { false,   true };
		String[]  axisName = { "X", "Y", "Z" };
		
		for (boolean velocity : truths) {
			String name = velocity ? "Velocity" : "Position";
			
			VectorComponent vectorComponent = registry.newInstance(VectorComponent.class, orbitalComponent);
			vectorComponent.setDisplayName( name );
			vectorComponent.save();
			
			for (int axis = 0; axis < 3; axis++) {
				CoordinateComponent coordinateComponent = registry.newInstance(CoordinateComponent.class, vectorComponent);
				coordinateComponent.setDisplayName(name + " " + axisName[axis]);
				coordinateComponent.setModel(new CoordinateModel(axis, velocity, orbitalComponent.getComponentId()));
			}			
		}
			
        return orbitalComponent;
        
	}

}
