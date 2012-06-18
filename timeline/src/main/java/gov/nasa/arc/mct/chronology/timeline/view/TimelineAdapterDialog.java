package gov.nasa.arc.mct.chronology.timeline.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A dialog window which allows users to specify lower and upper bounds on to 
 * which to map time-like data to a different chronological domain.
 * (For instance, mapping from an outline sequence - "1, 1.1, 1.2, 2" - to 
 * standard UNIX time)
 * @author vwoeltje
 *
 */
public class TimelineAdapterDialog extends JDialog {
	private static final long serialVersionUID = 4656019086472861048L;
	private TimelineView controlledView;
	private Callback     callback;
	private JTextField   startField = new JTextField(16);
	private JTextField   endField   = new JTextField(16);
	private JButton      okButton     = new JButton("Add")   ;
	private JButton      cancelButton = new JButton("Cancel");
	
	private static final String DIALOG_INSTRUCTIONS = 
		"<html>%s contains events on a time scale which is different from that currently displayed.<br/>" +
		"You may assign this sequence of events a new start and end time for this view.</html>";
	
	
	/**
	 * Creates a new dialog for soliciting user input about timeline bounds
	 * @param w the window from which to launch the dialog
	 * @param objectName the name of the MCT object whose chronology will be mapped (shown in the dialog)
	 * @param view the Timeline View which will receive the results of this operation
	 * @param callback a callback describing the behavior to be performed once data has been entered
	 */
	public TimelineAdapterDialog(Window w, String objectName, TimelineView view, Callback callback) {
		super(w);
		
		setLocationRelativeTo(w);
		controlledView = view;
		this.callback = callback;
		
		JPanel inputFields = new JPanel();
		inputFields.add(new JLabel("Start:"));
		inputFields.add(startField);
		inputFields.add(new JLabel("End:"));
		inputFields.add(endField);
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createHorizontalGlue());
		buttons.add(okButton);
		buttons.add(Box.createHorizontalStrut(4));
		buttons.add(cancelButton);
		buttons.add(Box.createHorizontalStrut(12));
		
		addListeners();

		JLabel instructions = new JLabel(String.format(DIALOG_INSTRUCTIONS, 
				objectName.length() > 16 ? objectName.substring(0, 15) + "..." : objectName));
		instructions.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(3,1));
        p.add(instructions);
		p.add(inputFields);
		p.add(buttons);
		
		getContentPane().add(p);
	}
	
	private void addListeners() {
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				callback.run(getStart(), getEnd());
				setVisible(false);
			}
		});
		startField.getDocument().addDocumentListener(new IntervalValidator(startField));
		endField.getDocument().addDocumentListener(new IntervalValidator(endField));
		okButton.setEnabled(false);
		startField.setText(controlledView.getChronologyBounds().getStart().toString());
		endField.setText(controlledView.getChronologyBounds().getEnd().toString());
	}
	
	private String getStart() {
		return startField.getText();
	}
	
	private String getEnd()   {
		return endField.getText();
	}

	private class IntervalValidator implements DocumentListener {
		private JTextField field;
		
		public IntervalValidator(JTextField field) {
			this.field = field;
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			field.setForeground( (controlledView.validateInstant(field.getText())) ? Color.BLACK : Color.RED);
			okButton.setEnabled(controlledView.validateInterval(startField.getText(), endField.getText()));
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
	
	/**
	 * An interface for describing behavior to invoke once a user has completed 
	 * and confirmed data entry into a TimelineAdapaterDialog
	 * @author vwoeltje
	 *
	 */
	public interface Callback {
		public void run(String start, String end);
	}
}
