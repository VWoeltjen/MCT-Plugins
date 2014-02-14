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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class TagSelectionDialog extends JDialog {
	private static final long serialVersionUID = 270779787729717592L;

	// Maintain selection by id
	private Set<String> selectedIds = new HashSet<String>();
	
	public TagSelectionDialog(
			List<AbstractComponent> repositories, 
			Collection<AbstractComponent> selected,
			Window parent) {
		super(parent);
		
		for (AbstractComponent c : selected) {
			selectedIds.add(c.getComponentId());
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		for (AbstractComponent repo : repositories) {
			panel.add(makeCheckBoxPanel(repo));
		}
		
		getContentPane().add(panel);
		
		pack();
	}
	
	private JComponent makeCheckBoxPanel(AbstractComponent repository) {
		JPanel p = new JPanel();
		
		p.setBorder(BorderFactory.createTitledBorder(repository.getDisplayName()));
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		for (AbstractComponent child : repository.getComponents()) {
			JCheckBox checkBox = new JCheckBox(child.getDisplayName());
			checkBox.setSelected( selectedIds.contains(child.getComponentId()) );
			checkBox.addActionListener( new Selector(child.getComponentId()) );
			p.add(checkBox);
		}
		
		return p;
	}

	private class Selector implements ActionListener {
		private String id;
		
		public Selector(String id) {
			this.id = id;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			Object src = evt.getSource();
			if (src instanceof JCheckBox) {
				boolean checked = ((JCheckBox) src).isSelected();
				if (checked) {
					selectedIds.add(id);
				} else {
					selectedIds.remove(id);
				}
			}
		}		
	}
	
}
