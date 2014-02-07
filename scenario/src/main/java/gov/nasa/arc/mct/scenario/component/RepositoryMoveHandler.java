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
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Handles movement from one repository to another, popping up a blocking 
 * dialog which serves two purposes:
 * 
 *  - Blocks user interaction until the move is complete
 *  - Notifies the user that a move has occurred (this is atypical)
 *
 */
public class RepositoryMoveHandler {
	private RepositoryComponent repositoryComponent;
	private Collection<AbstractComponent> addedComponents;
	
	public RepositoryMoveHandler(RepositoryComponent repositoryComponent,
			Collection<AbstractComponent> addedComponents) {
		super();
		this.repositoryComponent = repositoryComponent;
		this.addedComponents = addedComponents;
	}
	
	public void handle() {
		final SwingWorker<Map<String, Collection<String>>, ?> w = new RepositoryMoveWorker();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new RepositoryMoveDialog(w).setVisible(true);				
			}
		});
		
		w.execute();
	}
	
	private class RepositoryMoveDialog extends JDialog {
		private static final long serialVersionUID = 7688596759924866708L;
		
		public RepositoryMoveDialog(final SwingWorker<Map<String, Collection<String>>,?> worker) {
			super(null, Dialog.ModalityType.APPLICATION_MODAL);
			
			final JPanel panel = new JPanel();
			final JLabel label = new JLabel("Moving objects to " + repositoryComponent.getDisplayName() + "...");
			final JComponent details = new JPanel();
			final JProgressBar progress = new JProgressBar(0, 100);
			final JButton button = new JButton("OK");
			
			button.setEnabled(false);
			progress.setVisible(true);
			details.setVisible(false);
			
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					RepositoryMoveDialog.this.dispose();					
				}				
			});
			worker.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					progress.setValue(worker.getProgress());
					label.setVisible(!worker.isDone());
					progress.setVisible(!worker.isDone());
					details.setVisible(worker.isDone());
					button.setEnabled(worker.isDone());
					if (worker.isDone()) {
						details.removeAll();
						try {
							Map<String, Collection<String>> result = worker.get();
							String summary = "<html>";
							for (Entry<String, Collection<String>> moved : result.entrySet()) {
								String repoName = moved.getKey();
								summary += "<p> The following objects were moved out of " + repoName + ":";
								summary += "<ul>";
								for (String childName : moved.getValue()) {
									summary += "<li>" + childName + "</li>";
								}
								summary += "</ul></p>";
							}
							summary += "</html>";
							details.add(new JLabel(summary));
						} catch (InterruptedException e) {
						} catch (ExecutionException e) {
						}
					}
					pack();
				}				
			});
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.add(label);
			panel.add(progress);
			panel.add(details);
			panel.add(button);
			// Center all components
			for (Component c : panel.getComponents()) {				
				if (c instanceof JComponent) {
					((JComponent) c).setAlignmentX(JComponent.CENTER_ALIGNMENT);
				}
			}
			// Add some padding with a border
			panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
			add(panel);
			pack();
		}
	}
	
	private class RepositoryMoveWorker extends SwingWorker<Map<String, Collection<String>>, Void> {
		@Override
		protected Map<String, Collection<String>> doInBackground() throws Exception {
			// Store lists of components to remove from parents,
			// where parents are indicated by id (key to map)
			Map<String, Set<AbstractComponent>> toRemove = 
					new HashMap<String, Set<AbstractComponent>>();
			
			// getReferencingComponents may return different instances each time,
			// so store the latest returned instance in relation to its id
			Map<String, AbstractComponent> parentRepos = 
					new HashMap<String, AbstractComponent>();
			
			// For progress reporting
			int childIndex = 0;
			int childCount = addedComponents.size();
			
			// Build a list of things to remove
			for (AbstractComponent child : addedComponents) {
				Collection<AbstractComponent> parents = child.getReferencingComponents();
				int parentIndex = 0;
				int parentCount = parents.size();
				for (AbstractComponent parent : parents) {
					RepositoryCapability parentRepo = parent.getCapability(RepositoryCapability.class);				
					// Is another parent the same kind of repository?
					if (parentRepo != null && 
						parentRepo.getCapabilityClass().isAssignableFrom(repositoryComponent.getCapabilityClass())) {
						String parentId = parent.getComponentId();
						// Make sure we are not just looking at ourself
						if (!(repositoryComponent.getComponentId().equals(parentId))) {
							parentRepos.put(parentId, parent);
							if (!toRemove.containsKey(parentId)) {
								toRemove.put(parentId, new HashSet<AbstractComponent>());
							}
							toRemove.get(parentId).add(child);
						}					
					}					
					parentIndex++;
					setProgress((100 * childIndex + (parentIndex*childIndex/parentCount)) / childCount);
				}
				childIndex++;
				setProgress(100 * childIndex / childCount);
			}
			
			// Now, remove them
			for (String id : parentRepos.keySet()) {
				parentRepos.get(id).removeDelegateComponents(toRemove.get(id));
			}
			
			// Finally, persist
			PlatformAccess.getPlatform().getPersistenceProvider().persist(parentRepos.values());
			
			// Then, prepare return values (display names)
			Map<String, Collection<String>> result = new HashMap<String, Collection<String>>();
			for (String id : parentRepos.keySet()) {
				Collection<String> removed = new ArrayList<String>();
				String name = parentRepos.get(id).getDisplayName();
				for (AbstractComponent child : toRemove.get(id)) {
					removed.add(child.getDisplayName());
				}
				result.put(name, removed);
			}		
			
			return result;
		}		
	}
	
}
