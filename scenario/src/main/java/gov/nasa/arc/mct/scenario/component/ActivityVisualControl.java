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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class ActivityVisualControl extends CustomVisualControl {
	private static final long serialVersionUID = 260628819696786275L;
	private List<AbstractComponent> tags = new ArrayList<AbstractComponent>();
	private JPanel tagPanel = new JPanel();
	private JComboBox comboBox;
	
	public ActivityVisualControl() {
		setLayout(new BorderLayout());
		comboBox = makeComboBox();
		add(comboBox, BorderLayout.NORTH);
		add(tagPanel, BorderLayout.CENTER);
	}
	
	@Override
	public void setValue(Object value) {
		tagPanel.removeAll();
		tags.clear();
		if (value instanceof List) {
			for (Object element : (List<?>) value) {
				if (element instanceof AbstractComponent) {
					add(LabelView.VIEW_INFO.createView((AbstractComponent) element));
					tags.add((AbstractComponent) element);
				}
			}
		}
	}

	@Override
	public Object getValue() {
		return tags;
	}

	@Override
	public void setMutable(boolean mutable) {
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
			}
		}
		
		JComboBox comboBox = new JComboBox(listItems.toArray());
		comboBox.setRenderer(new ListCellRenderer() {
			private JLabel label = new JLabel(); // Re-use
			private Color c = label.getForeground();
			@Override
			public Component getListCellRendererComponent(JList list,
					Object item, int index, boolean isSelected, boolean hasFocus) {
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
				
				label.setForeground(c);
				
				return label;
			}			
		});
		return comboBox;
	}
	
	
}
