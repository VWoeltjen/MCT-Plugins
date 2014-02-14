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
import gov.nasa.arc.mct.scenario.view.LabelView;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Provides the user interface for editing an activity's 
 * tags from the Info View.
 * @author vwoeltje
 *
 */
public class ActivityVisualControl extends CustomVisualControl {
	private static final long serialVersionUID = 260628819696786275L;
	private List<AbstractComponent> tags = new ArrayList<AbstractComponent>();
	private JPanel panel = new JPanel();
	private JButton editButton = new JButton("+");
	private Color foreground;
	
	/**
	 * Create a new visual control for editing children of an activity, 
	 * based on the type of capability they expose.
	 * @param capabilityClass the exposed capability
	 * @param componentClass class to be used for create actions (null to disallow create)
	 */
	public ActivityVisualControl(ComponentTypeInfo componentInfo,
			final List<AbstractComponent> repositories) {

		JPanel editButtonPanel = new JPanel(new BorderLayout());
		
		setLayout(new BorderLayout());
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		foreground = new JLabel().getForeground();
		add(panel, BorderLayout.CENTER);
		add(editButtonPanel, BorderLayout.EAST);
		
		editButtonPanel.add(editButton, BorderLayout.NORTH);
		editButton.setMargin(new Insets(0,3,0,3));
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new TagSelectionDialog(
						repositories, 
						tags, 
						ActivityVisualControl.this, 
						SwingUtilities.getWindowAncestor(editButton))
					.setVisible(true);
			}			
		});
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
		editButton.setEnabled(mutable);
		rebuildTagPanel();
	}
	
	public void setContents(List<AbstractComponent> contents) {
		setValue(contents);
		fireChange();
	}

	private void rebuildTagPanel() {
		panel.removeAll();
		for (AbstractComponent t : tags) {
			JComponent view = LabelView.VIEW_INFO.createView(t);
			view.setForeground(foreground);
			panel.add(view);
		}
		panel.revalidate();
		panel.repaint();
	}
}
