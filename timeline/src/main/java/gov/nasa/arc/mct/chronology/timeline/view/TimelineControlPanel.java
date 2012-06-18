package gov.nasa.arc.mct.chronology.timeline.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a set of controls to modify settings for a Timeline view.
 * @author vwoeltje
 *
 */
public class TimelineControlPanel extends JPanel {
	private static final long serialVersionUID = 8774835022131161491L;
	private final static Logger LOGGER = LoggerFactory.getLogger(TimelineControlPanel.class);
	
	private GridBagConstraints constraints = initialConstraints();
	
	private TimelineView controlledView;
	
	private JTextField startField, endField;
	private JButton    applyButton;
	
	/**
	 * Create a new control panel for a Timeline view
	 * @param timelineView the view whose settings will be manipulated by this control panel
	 */
	public TimelineControlPanel(TimelineView timelineView) {
		controlledView = timelineView;
		
		setLayout(new GridBagLayout());
		JPanel intervalPanel = makeSubPanel("Time Interval");
		intervalPanel.add(new JLabel("Start: "));
		intervalPanel.add(startField = new JTextField(16));
		intervalPanel.add(new JLabel("End: "  ));
		intervalPanel.add(endField   = new JTextField(16));
		intervalPanel.add(applyButton = new JButton("Apply"));
		startField.setText(controlledView.getChronologyBounds().getStart().toString());
		endField.setText  (controlledView.getChronologyBounds().getEnd().toString());
		
		startField.getDocument().addDocumentListener(new IntervalValidator(startField));
		endField.getDocument().addDocumentListener(new IntervalValidator(endField));
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!controlledView.setChronologyBounds(startField.getText(), endField.getText())) {
					LOGGER.warn("Failed to set chronology bounds, but start & end times appeared valid.");
				} 
			}
		});
	}
	
	/**
	 * Populate the data entry objects in the control panel with values in use by the actual view. 
	 */
	public void update() {
		startField.setText(controlledView.getChronologyBounds().getStart().toString());
		endField.setText  (controlledView.getChronologyBounds().getEnd().toString());		
	}
	
	
	private GridBagConstraints initialConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor  = GridBagConstraints.NORTH;  
		constraints.weightx = 1.0;
		constraints.weighty = 0.5;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;		
		constraints.gridy = 0;
		return constraints;
	}

	private JPanel makeSubPanel(String title) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(title));
		constraints.gridy++;
		add (panel, constraints);
		return panel;
	}
	
	private class IntervalValidator implements DocumentListener {
		private JTextField field;
		
		public IntervalValidator(JTextField field) {
			this.field = field;
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			field.setForeground( (controlledView.validateInstant(field.getText())) ? Color.BLACK : Color.RED);
			applyButton.setEnabled(controlledView.validateInterval(startField.getText(), endField.getText()));
			field.repaint();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			changedUpdate(arg0);
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			changedUpdate(arg0);
		}
		
	}
}
