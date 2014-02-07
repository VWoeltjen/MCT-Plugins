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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
		SwingWorker<Void, Void> w = new RepositoryMoveWorker();
		
		w.execute();
	}
	
	private class RepositoryMoveWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			Map<AbstractComponent, Set<AbstractComponent>> otherRepositories = 
					new HashMap<AbstractComponent, Set<AbstractComponent>>();
			
			int i = 0, j = 0;
			int childCount = addedComponents.size();
			
			// Build a list of things to remove
			for (AbstractComponent child : addedComponents) {
				Collection<AbstractComponent> parents = child.getReferencingComponents();
				int parentCount = parents.size();
				for (AbstractComponent parent : parents) {
					RepositoryCapability parentRepo = parent.getCapability(RepositoryCapability.class);				
					// Is another parent the same kind of repository?
					if (parentRepo != null && 
						parentRepo.getCapabilityClass().isAssignableFrom(repositoryComponent.getCapabilityClass())) {
						// Make sure we are not just looking at ourself
						if (!(repositoryComponent.getComponentId().equals(parent.getComponentId()))) {
							if (!otherRepositories.containsKey(parent)) {
								otherRepositories.put(parent, new HashSet<AbstractComponent>());
							}
							otherRepositories.get(parent).add(child);
						}					
					}
					setProgress((100 * i + (j*i/parentCount)) / childCount);
				}
				i++;
				setProgress(100 * i / childCount);
			}
			
			// Now, remove them
			for (Entry<AbstractComponent, Set<AbstractComponent>> otherRepo : otherRepositories.entrySet()) {
				otherRepo.getKey().removeDelegateComponents(otherRepo.getValue());
			}
			
			// Finally, persist
			PlatformAccess.getPlatform().getPersistenceProvider().persist(otherRepositories.keySet());
			
			return null;
		}		
	}
	
}
