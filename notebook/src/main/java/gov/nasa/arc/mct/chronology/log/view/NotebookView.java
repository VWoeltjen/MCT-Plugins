package gov.nasa.arc.mct.chronology.log.view;

import gov.nasa.arc.mct.chronology.Chronology;
import gov.nasa.arc.mct.chronology.event.ChronologicalEvent;
import gov.nasa.arc.mct.chronology.event.UNIXTimeInstant;
import gov.nasa.arc.mct.chronology.log.component.ComponentRegistryAccess;
import gov.nasa.arc.mct.chronology.log.component.LogEntry;
import gov.nasa.arc.mct.chronology.log.component.UserLogComponent;
import gov.nasa.arc.mct.chronology.log.component.UserLogEntryComponent;
import gov.nasa.arc.mct.chronology.reference.view.ReferenceView;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.roles.events.AddChildEvent;
import gov.nasa.arc.mct.roles.events.PropertyChangeEvent;
import gov.nasa.arc.mct.roles.events.ReloadEvent;
import gov.nasa.arc.mct.roles.events.RemoveChildEvent;
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class NotebookView extends View {
	private static final long serialVersionUID = 1129569325053980669L;
	private static final DateFormat DATE_FORMAT = UNIXTimeInstant.DATE_FORMAT;
	private static final Color  HIGHLIGHT = new Color(250, 250, 220);
	
	private JPanel        dataEntryPanel = new JPanel();
	private ReferenceArea referenceArea;
	
	private LogEntry<String> logEntry = null;
	private UserLogComponent logComponent;
	private Set<Component>   highlightedComponents = new HashSet<Component>();
	
	public static final Border ENTRY_BORDER = new CurvedBorder(12,12,12,12); 
	public static String VIEW_ROLE_NAME = "Notebook";

	public NotebookView(AbstractComponent ac, ViewInfo vi) {
		super(ac, vi);
		
		setupLogComponents();
		
		setupView();
	}
	
	@SuppressWarnings("unchecked")
	private void setupView() {
		JPanel      logPanel   = new JPanel() {
			private static final long serialVersionUID = 5232272971048714421L;

			public void paint(Graphics g) {
				int highlightStart = 0;
				boolean highlight  = false;
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(getForeground());
				for (Component c : getComponents()) {
					int y = c.getY() - 4;
					if (highlight) {
						if (y > highlightStart) {
							g.setColor(HIGHLIGHT);
							g.fillRect(0, highlightStart + 1, getWidth(), y - highlightStart);
							g.setColor(getForeground());
							highlight = false;
						}
					} else if (highlightedComponents.contains(c)) {
						highlight = true;
						highlightStart = y;
					}
					g.drawLine(0, y, getWidth(), y);
				}
				super.paint(g);
			}
		};
	    
		setBackground(Color.LIGHT_GRAY.brighter());
		setForeground(new Color(200, 220, 255));
		
		logPanel.setBackground(Color.LIGHT_GRAY.brighter());
		logPanel.setForeground(new Color(200, 220, 255));
		logPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		//gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridy = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		
		if (logComponent != null) {
			logComponent.getComponents(); // Force loading of referenced components

			Chronology<?> chron = logComponent.getCapability(Chronology.class);
			if (chron != null) {
				for (ChronologicalEvent<?> event : chron.getEvents()) {
					Object eventInfo = event.getEventInfo();
					if (eventInfo instanceof LogEntry && 
							((LogEntry<?>) eventInfo).canCast(String.class)) {
						buildEntryPanel((LogEntry<? extends String>) eventInfo, logPanel, gbc); 				
						gbc.gridy++;
					}
				}
			}
		} else if (logEntry != null) {
			buildEntryPanel(logEntry, logPanel, gbc);
		}
		
		prepareDataEntryPanel(logPanel, gbc);
	
		setLayout(new BorderLayout());
		
		removeAll();
		this.setOpaque(false);
		logPanel.setOpaque(false);
		add(logPanel, BorderLayout.NORTH);
		revalidate();
	}
	
	private void prepareDataEntryPanel(JComponent panel, GridBagConstraints gbc) {
		if (dataEntryPanel.getComponentCount() == 0) { //...only prepare once
			
			// Disallow input for notebooks on canvas (etc)
			if (!isEditable()) return;
			
			final JLabel prompt = new JLabel("<html><i>Add new entry...</i></html>");
			final JTextArea textArea = new JTextArea();
			final JLabel   updateButton = new JLabel("+");
			referenceArea = new ReferenceArea();
			
			referenceArea.setVisible(false);
			updateButton.setVisible(false);
			textArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			textArea.setDropTarget(referenceArea.getDropTarget());
			prompt.setForeground(Color.GRAY);
			dataEntryPanel = new JPanel();
			dataEntryPanel.setOpaque(false);
			dataEntryPanel.add(prompt);
			MouseListener dataEntryMouseListener = new MouseListener() {
				boolean active = false;

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (arg0.getSource().equals(dataEntryPanel)) { 
						if (active) return;
						dataEntryPanel.removeAll();
						dataEntryPanel.add(textArea);
						dataEntryPanel.add(updateButton);
						referenceArea.setVisible(!referenceArea.getReferences().isEmpty());
						textArea.grabFocus();
						revalidate();
						repaint();
						active = true;
					} else if (arg0.getSource().equals(updateButton)) {
						if (updateButton.isVisible() && active) {
							AbstractComponent comp = getManifestedComponent();

							if (comp instanceof UserLogComponent) {
							
								List<AbstractComponent> references = referenceArea.getReferences();
								((UserLogComponent) comp).addEntry(textArea.getText(), references);
								//comp.addDelegateComponents(references);
								//PlatformAccess.getPlatform().getPersistenceProvider().startRelatedOperations();
								comp.save();
								//PlatformAccess.getPlatform().getPersistenceProvider().completeRelatedOperations(true);
								
								textArea.setText("");
								updateButton.setVisible(false);
								referenceArea.setVisible(false);
								referenceArea.clear();
								
								dataEntryPanel.removeAll();
								dataEntryPanel.add(prompt);
								revalidate();
								active = false;	
								
								setupView();
							}
						}
					} else { // NOT the data entry panel - so hide it
						if (!active) return;
						dataEntryPanel.removeAll();
						dataEntryPanel.add(prompt);
						referenceArea.setVisible(false);
						revalidate();
						repaint();
						active = false;					
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {}

				@Override
				public void mouseExited(MouseEvent arg0) {}

				@Override
				public void mousePressed(MouseEvent arg0) {}

				@Override
				public void mouseReleased(MouseEvent arg0) {}

			};
			textArea.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent arg0) {}

				@Override
				public void keyReleased(KeyEvent arg0) {}

				@Override
				public void keyTyped(KeyEvent arg0) {
					updateButton.setVisible(!textArea.getText().isEmpty());
				}
			});
			dataEntryPanel.addMouseListener(dataEntryMouseListener);
			updateButton.addMouseListener(dataEntryMouseListener);
			addMouseListener(dataEntryMouseListener);
		}
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		panel.add(dataEntryPanel, gbc);
		
		gbc.gridx = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(referenceArea, gbc);
	}
	
	private void setupLogComponents() {
		AbstractComponent comp = getManifestedComponent();
		if (comp instanceof UserLogComponent) {
			logComponent = (UserLogComponent) comp;
		} else {
			for (AbstractComponent parent : comp.getReferencingComponents()) {
				if (parent instanceof UserLogComponent) logComponent = (UserLogComponent) parent;	
			}
			if (comp instanceof UserLogEntryComponent) {
				logEntry = ((UserLogEntryComponent) comp).getEntry();
			}
		}
	}
	
	private boolean isEditable() {
		return (getManifestedComponent().getWorkUnitDelegate() == null ||
				getManifestedComponent() != logComponent);
	}
	
	private JComponent buildEntryPanel(LogEntry<? extends String> entry, JComponent panel, GridBagConstraints gbc) {
		//JPanel panel = new JPanel();
		//panel.setLayout(new BorderLayout());
		//JComponent panel = this;		
		gbc.gridx   = 0;
		gbc.weightx = 0.0;
//		JLabel timeLabel = new JLabel("<html>" +
//					         "<i>" + DATE_FORMAT.format(new Date(entry.getEntryTime())) + "</i>: " + 
//				             "</html>"); //, gbc);// BorderLayout.WEST);
	
		panel.add(new EditableTimeLabel(entry), gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		JLabel contents = new JLabel("<html>" + (entry == logEntry ? "<b>" : "") +
	             entry.getEntry().replaceAll("\n", "\n<br/>") +
	             (entry == logEntry ? "</b>" : "") + 
	             "</html>");
		panel.add(contents, gbc); //BorderLayout.CENTER);
		if (entry != null && logEntry != null && entry.getEntryTime() == logEntry.getEntryTime()) {
			highlightedComponents.add(contents);
		}

		JPanel referencePanel = new JPanel();
		referencePanel.setLayout(new BoxLayout(referencePanel, BoxLayout.PAGE_AXIS));
		referencePanel.setOpaque(false);
		ComponentRegistry registry = ComponentRegistryAccess.getComponentRegistry();
		for (String reference : entry.getReferencedIDs()) {
			AbstractComponent comp = registry.getComponent(reference);
			if (comp != null) {
				referencePanel.add(new NotebookPanel(comp, entry.getEntryTime()));
			}
		}
		
		gbc.gridx = 2;
		gbc.weightx = 0.0;
		panel.add(referencePanel, gbc);// BorderLayout.EAST);
		//panel.setBorder(ENTRY_BORDER);
		
		return panel;
	}
	

	
	@Override
	public void updateMonitoredGUI() {
		setupView();
	}
	
	

	@Override
	public void updateMonitoredGUI(AddChildEvent event) {
		updateMonitoredGUI();
	}

	@Override
	public void updateMonitoredGUI(ReloadEvent event) {
		updateMonitoredGUI();
	}

	@Override
	public void updateMonitoredGUI(RemoveChildEvent event) {
		updateMonitoredGUI();
	}

	@Override
	public void updateMonitoredGUI(
			gov.nasa.arc.mct.roles.events.FocusEvent event) {
		updateMonitoredGUI();
	}

	@Override
	public void updateMonitoredGUI(PropertyChangeEvent event) {
		updateMonitoredGUI();
	}

	public void paint(Graphics g) {
//		for (int y = 0; y < getHeight(); y += backgroundTile.getHeight()) {
//			for (int x = 0; x < getWidth(); x += backgroundTile.getWidth()) {
//				//g.drawImage(backgroundTile, x, y, this);
//			}
//		}
//		g.setColor(Color.CYAN);
//		for (Component c : getComponents()) {
//			g.drawLine(c.getX(), c.getY(), c.getWidth(), c.getHeight());
//		}
		int y = 0;
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		for (Component c : getComponents()) { //Find bottom of visible components
			int bottom = c.getY() + c.getHeight() + 1;
			if (bottom > y) y = bottom;
		}
		for (y = y; y < getHeight(); y += 24) {
			g.drawLine(0, y, getWidth(), y);
		}
		super.paint(g);
	}
	
	
	private static class CurvedBorder extends EmptyBorder {
		private static final long serialVersionUID = 6190785509909919993L;

		public CurvedBorder(Insets insets) {
			super(insets);
		}
		
		public CurvedBorder(int top, int left, int bottom, int right) {
			super(top, left, bottom, right);
		}

		@Override
		public boolean isBorderOpaque() {
			return false;
		}

		@Override
		public void paintBorder(Component comp, Graphics g, int x,
				int y, int w, int h) {
			Insets insets = getBorderInsets();
			
			if (comp.getParent() != null) {
				g.setColor(comp.getParent().getBackground());
				g.fillRect(x, y, w, y + insets.top);
				g.fillRect(x, y + h - insets.bottom, w, insets.bottom);
			}
			
			g.setColor(comp.getBackground());
			g.fillRect(x, y + insets.top, insets.left, h - insets.bottom - insets.top);
			g.fillRect(x + w - insets.right, y + insets.top, insets.right, h - insets.bottom - insets.top);
			g.fillRect(x + insets.left, y, w - insets.right - insets.left, insets.top);
			g.fillRect(x + insets.left, y + h - insets.bottom, w - insets.right - insets.left, insets.bottom);
			g.fillArc(x, y, insets.left * 2, insets.top * 2, 90, 90);
			g.fillArc(x+w-insets.right*2, y, insets.right * 2, insets.top * 2, 0, 90);
			g.fillArc(x, y+h-insets.bottom*2, insets.left * 2, insets.bottom * 2, 180, 90);
			g.fillArc(x+w-insets.right*2, y+h-insets.bottom*2, insets.right * 2, insets.bottom * 2, 270, 90);
		}
		
		
	}
	
	private class ReferenceArea extends JScrollPane {
		private static final long serialVersionUID = -4640358863656637380L;
		private List<AbstractComponent> references;
		private JPanel                  referencePanel;
		
		public ReferenceArea() {
			references = new ArrayList<AbstractComponent>();
			referencePanel = new JPanel();
			referencePanel.setLayout(new BoxLayout(referencePanel, BoxLayout.PAGE_AXIS));
			getViewport().add(referencePanel);
			setDropTarget( new DropTarget() {
				private static final long serialVersionUID = 1L;
				public void drop (DropTargetDropEvent event) {
					if (event.getTransferable().isDataFlavorSupported(View.DATA_FLAVOR)) {
						try {
							for (View view : (View[]) event.getTransferable().getTransferData(View.DATA_FLAVOR)) {
								AbstractComponent comp = view.getManifestedComponent();
//								if (comp.getMasterComponent() != null) {
//									comp = comp.getMasterComponent();
//								} //TODO: Work unit delegate?
								if (comp.getWorkUnitDelegate() != null) {
									comp = comp.getWorkUnitDelegate();
								}
								addReference(comp);
							}
						} catch (Exception ioe) {
							event.rejectDrop();
							return; //TODO: Log?
						}
					} else {
						event.rejectDrop();
						return;
					}
				}
			} );
		}
		
		private void addReference(AbstractComponent comp) {
			ReferenceView     view = (ReferenceView) ReferenceView.VIEW_INFO.createView(comp);
			view.setBackground(getBackground());
			view.setDataVisibility(false);
			referencePanel.add(view);
			view.setAlignmentX(Component.LEFT_ALIGNMENT);
			references.add(comp);
			setVisible(true);
			revalidate();
		}
		
		public List<AbstractComponent> getReferences() {
			return references;
		}
		
		public void clear() {
			references.clear();
			referencePanel.removeAll();
		}
	}
		
	private class EditableTimeLabel extends JPanel {
		private static final long serialVersionUID = -3347141859567304710L;

		private LogEntry<? extends String> entry;
		private JLabel     timeLabel  = new JLabel();
		private JTextField entryField = new JTextField();
		
		public EditableTimeLabel(LogEntry<? extends String> e) {
			this.entry = e;
			String time = DATE_FORMAT.format(new Date(entry.getEntryTime()));
			timeLabel.setText(time);
			entryField.setText(time);
			timeLabel.setFont(timeLabel.getFont().deriveFont(Font.ITALIC));
			setOpaque(false);
			add(timeLabel);
			add(entryField);
			timeLabel.setVisible(true);
			entryField.setVisible(false);
			timeLabel.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (isEditable()) {
						timeLabel.setVisible(false);
						entryField.setVisible(true);
						entryField.grabFocus();
						repaint();
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
				}
				
			});
			
			entryField.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String newTime = entryField.getText();
					try {
						UNIXTimeInstant instant = UNIXTimeInstant.DOMAIN.convertToInstant(newTime);
						timeLabel.setText(entryField.getText());
						entry.setEntryTime(instant.getTimeMillis());
						//getManifestedComponent().save();
						PlatformAccess.getPlatform().getPersistenceProvider().startRelatedOperations();
						for (AbstractComponent child : getManifestedComponent().getComponents())
							if (child instanceof UserLogEntryComponent)
								if(((UserLogEntryComponent) child).getEntry().equals(entry))
									child.save(); // Entry actually exists in some child - probably!
						PlatformAccess.getPlatform().getPersistenceProvider().completeRelatedOperations(true);
					} catch (ParseException p) {
						entryField.setText(timeLabel.getText());
					}
					entryField.setVisible(false);
					timeLabel.setVisible(true);
					repaint();
				}
				
			});
			
			entryField.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent arg0) {
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if (entryField.isVisible()) {
						entryField.setVisible(false);
						entryField.setText(timeLabel.getText());
						timeLabel.setVisible(true);
						repaint();
					}
				}
				
			});
		}
		
	}

	
}
