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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Check box dialog for specifying which tags (or activity types) 
 * apply to a given Activity. This is triggered from the Info View 
 * for an Activity, by way of its custom visual control.
 */
public class TagSelectionDialog extends JDialog {
	private static final long serialVersionUID = 270779787729717592L;

	// Maintain selection by id
	private Set<String> originalIds = new HashSet<String>();
	private Set<String> selectedIds = new HashSet<String>();
	
	// Refer to components that were part of original selection first
	private Map<String, AbstractComponent> originalComponents = 
			new HashMap<String, AbstractComponent>();
	// Then, refer to components which were loaded by way of repository
	private Map<String, AbstractComponent> loadedComponents = 
			new HashMap<String, AbstractComponent>();
	
	// Result of the selection; null if canceled or incomplete
	private List<AbstractComponent> result = null;
	
	
	public TagSelectionDialog(
			List<AbstractComponent> repositories, 
			Collection<AbstractComponent> selected,
			Window parent) {
		super(parent);
		
		this.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);		
		
		for (AbstractComponent c : selected) {
			String id = c.getComponentId();
			selectedIds.add(id);
			originalIds.add(id);
			originalComponents.put(id, c);
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		for (AbstractComponent repo : repositories) {
			panel.add(makeCheckBoxPanel(repo));
		}
		
		panel.add(makeButtons());
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		getContentPane().add(panel);
		
		pack();
	}
	
	private boolean isModified() {
		boolean m = false;
		for (String id : originalIds) {
			m |= !(selectedIds.contains(id));
		}
		return m || (originalIds.size() != selectedIds.size());
	}
	
	private JComponent makeButtons() {
		JPanel p = new JPanel();
		
		JButton cancel = new JButton("Cancel");
		JButton ok = new JButton("OK");
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				TagSelectionDialog.this.dispose();
			}			
		});
		
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (isModified()) {
					List<AbstractComponent> value = new ArrayList<AbstractComponent>();
					
					for (String id : selectedIds) {
						value.add(originalComponents.containsKey(id) ?
								originalComponents.get(id) : 
								loadedComponents.get(id)
								);
					}
					
					result = value;
				}
				TagSelectionDialog.this.dispose();
			}			
		});
		
		p.add(ok);
		p.add(cancel);
		p.setBorder(BorderFactory.createEmptyBorder(17, 0, 0, 0));
		p.setAlignmentX(CENTER_ALIGNMENT);
		return p;
	}
	
	/**
	 * Get the list of components selected by the user during the 
	 * operation of this dialog. If the user cancelled or otherwise has 
	 * not confirmed the operation by clicking "OK", the result 
	 * will be null.
	 * @return a list of selected components, or null if cancelled
	 */
	public List<AbstractComponent> getResult() {
		return result;
	}
	
	private JComponent makeCheckBoxPanel(AbstractComponent repository) {
		JPanel p = new JPanel();
		
		p.setBorder(BorderFactory.createTitledBorder(repository.getDisplayName()));
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		for (AbstractComponent child : repository.getComponents()) {
			String id = child.getComponentId();
			loadedComponents.put(id, child);
			
			JCheckBox checkBox = new JCheckBox(child.getDisplayName());
			checkBox.setSelected( selectedIds.contains(id) );
			checkBox.addActionListener( new Selector(id) );
			checkBox.setAlignmentX(LEFT_ALIGNMENT);
			p.add(checkBox);
		}	
		JPanel rigid = new JPanel();
		p.add(rigid);
		rigid.setAlignmentX(LEFT_ALIGNMENT);
		rigid.add(Box.createRigidArea(new Dimension(240,1)));		
		
		p.setAlignmentX(CENTER_ALIGNMENT);
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
