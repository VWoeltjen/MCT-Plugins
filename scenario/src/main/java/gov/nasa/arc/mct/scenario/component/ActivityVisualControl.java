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
import gov.nasa.arc.mct.gui.CustomVisualControl;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.scenario.view.LabelView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class ActivityVisualControl extends CustomVisualControl {
	private static final long serialVersionUID = 260628819696786275L;
	private List<AbstractComponent> tags = new ArrayList<AbstractComponent>();
	private JPanel tagPanel = new JPanel();
	private JComboBox comboBox;
	private Color foreground;
	
	public ActivityVisualControl() {
		setLayout(new BorderLayout());
		comboBox = makeComboBox();
		foreground = new JLabel().getForeground();
		add(comboBox, BorderLayout.NORTH);
		add(tagPanel, BorderLayout.CENTER);
	}
	
	@Override
	public void setValue(Object value) {
		tags.clear();
		if (value instanceof List) {
			for (Object element : (List<?>) value) {
				if (element instanceof AbstractComponent) {
					tags.add((AbstractComponent) element);
				}
			}
		}
		rebuildTagPanel();
	}

	@Override
	public Object getValue() {
		return tags;
	}

	@Override
	public void setMutable(boolean mutable) {
	}

	private void rebuildTagPanel() {
		tagPanel.removeAll();
		for (AbstractComponent t : tags) {
			tagPanel.add(new RemovableTag(t));
		}
	}
	
	private void addTag(AbstractComponent tag) {
		if (!tags.contains(tag)) {
			tags.add(tag);
		}		
		rebuildTagPanel();
		tagPanel.revalidate();
		tagPanel.repaint();
		fireChange();
	}
	
	private void removeTag(AbstractComponent tag) {
		if (tags.contains(tag)) {
			tags.remove(tag);
		}		
		rebuildTagPanel();
		tagPanel.revalidate();
		tagPanel.repaint();
		fireChange();
	}
	
	private JComboBox makeComboBox() {
		List<Object> listItems = new ArrayList<Object>();
		for (AbstractComponent comp : 
			PlatformAccess.getPlatform().getBootstrapComponents()) {
			RepositoryCapability repo = comp.getCapability(RepositoryCapability.class);
			if (repo != null && 
				TagCapability.class.isAssignableFrom(repo.getCapabilityClass())) {
				// First, add repo as a section heading
				listItems.add(comp);
				// Add all tags underneath
				for (AbstractComponent child : comp.getComponents()) {					
					listItems.addAll(child.getCapabilities(TagCapability.class));
				}
				listItems.add(Box.createVerticalStrut(8)); // Blank space
			}
		}

		JComboBox comboBox = new JComboBox(listItems.toArray());
		
		comboBox.setEditor(new ComboBoxEditor() {
			private JLabel label = new JLabel("- Add a Tag -");

			@Override
			public void addActionListener(ActionListener listener) {}

			@Override
			public Component getEditorComponent() {
				return label;
			}

			@Override
			public Object getItem() {
				return label.getText();
			}

			@Override
			public void removeActionListener(ActionListener listener) {}

			@Override
			public void selectAll() {}

			@Override
			public void setItem(Object item) {}			
		});
 		comboBox.setEditable(true);

		comboBox.setRenderer(new ListCellRenderer() {
			private JLabel label = new JLabel(); // Re-use
			@Override
			public Component getListCellRendererComponent(JList list,
					Object item, int index, boolean isSelected, boolean hasFocus) {
				if (item instanceof Component) {
					return (Component) item;
				}
				
				label.setFont(label.getFont().deriveFont(
					item instanceof TagCapability ? Font.PLAIN : 
					item instanceof AbstractComponent ? Font.ITALIC :
					Font.BOLD));
						
				label.setIcon(item instanceof TagCapability ?
					((TagCapability)item).getComponentRepresentation().getAsset(ImageIcon.class) :
					null);
				
				label.setText(item instanceof AbstractComponent ?
					((AbstractComponent)item).getDisplayName() :
					item.toString());
				
				label.setForeground(foreground);
				
				return label;
			}			
		});
		
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				Object source = event.getSource();
				if (source instanceof JComboBox) {
					Object item = ((JComboBox) source).getSelectedItem();
					if (item instanceof TagCapability) {
						addTag(((TagCapability) item).getComponentRepresentation());
					}
				}				
			}			
		});
		
		return comboBox;
	}

	private class RemovableTag extends JPanel implements ActionListener {
		private static final long serialVersionUID = 4900258333156735699L;
		private AbstractComponent tagComponent;

		public RemovableTag(AbstractComponent tagComponent) {
			super();
			setLayout(new BorderLayout());
			this.tagComponent = tagComponent;
			JComponent label = LabelView.VIEW_INFO.createView(tagComponent);
			label.setForeground(foreground);
			JButton icon = new JButton(X_ICON);
			icon.setBorder(null);
			icon.setFocusPainted(false);
			icon.setBorderPainted(false);
			icon.setContentAreaFilled(false);
			icon.setOpaque(false);
			icon.setBackground(comboBox.getBackground().darker());
			icon.setForeground(foreground);
			icon.addActionListener(this);
			add (label, BorderLayout.CENTER);
			add (icon, BorderLayout.EAST);
			setOpaque(false);
			setBackground(comboBox.getBackground());
			setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		}
		
		@Override
		public void paintComponent(Graphics g) {
			// Draw with smooth edges
			if (g instanceof Graphics2D) {
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
						                          RenderingHints.VALUE_ANTIALIAS_ON);
			}
			g.setColor(getBackground());
			g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
			super.paintComponent(g);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			removeTag(tagComponent);
		}
	}
	
	private static final Icon X_ICON = new Icon() {
		@Override
		public int getIconHeight() {			
			return 12;
		}

		@Override
		public int getIconWidth() {
			return 12;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(c.getBackground());
			g.fillOval(x, y, getIconWidth(), getIconHeight());
			g.setColor(c.getForeground());
			g.drawLine(x +   getIconWidth()/4  , y +   getIconHeight()/4  , 
					   x + 3*getIconWidth()/4-1, y + 3*getIconHeight()/4-1);
			g.drawLine(x +   getIconWidth()/4  , y + 3*getIconHeight()/4-1, 
					   x + 3*getIconWidth()/4-1, y +   getIconHeight()/4  );

		}
		
	};
	
}
