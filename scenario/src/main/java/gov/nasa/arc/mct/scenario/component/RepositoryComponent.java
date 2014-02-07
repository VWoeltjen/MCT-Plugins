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
import gov.nasa.arc.mct.components.Bootstrap;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Abstract superclass for components which serve as canonical 
 * repositories for objects of other types. (e.g. User Tags, 
 * Mission Activity Types, et cetera...)
 */
public abstract class RepositoryComponent extends AbstractComponent implements RepositoryCapability, Bootstrap {

	@Override
	protected void addDelegateComponentsCallback(
			Collection<AbstractComponent> childComponents) {
		super.addDelegateComponentsCallback(childComponents);
		
		Map<AbstractComponent, Set<AbstractComponent>> otherRepositories = 
				new HashMap<AbstractComponent, Set<AbstractComponent>>();
		
		// Build a list of things to remove
		for (AbstractComponent child : childComponents) {
			for (AbstractComponent parent : child.getReferencingComponents()) {
				RepositoryCapability parentRepo = parent.getCapability(RepositoryCapability.class);				
				// Is another parent the same kind of repository?
				if (parentRepo != null && 
					parentRepo.getCapabilityClass().isAssignableFrom(getCapabilityClass())) {
					// Make sure we are not just looking at ourself
					if (!(getComponentId().equals(parent.getComponentId()))) {
						if (!otherRepositories.containsKey(parent)) {
							otherRepositories.put(parent, new HashSet<AbstractComponent>());
						}
						otherRepositories.get(parent).add(child);
					}					
				}
			}
		}
		
		// Now, remove them
		for (Entry<AbstractComponent, Set<AbstractComponent>> otherRepo : otherRepositories.entrySet()) {
			otherRepo.getKey().removeDelegateComponents(otherRepo.getValue());
		}
		
		// Finally, persist
		PlatformAccess.getPlatform().getPersistenceProvider().persist(otherRepositories.keySet());
	}

	@Override
	public <T> T handleGetCapability(Class<T> capabilityClass) {	
		return
			capabilityClass.isAssignableFrom(getClass()) ? 
					capabilityClass.cast(this) :
			super.handleGetCapability(capabilityClass);
	}

	// TODO: Use this to support the Group capability
	@Override
	public String getUserScope() {
		return getCreator(); //model.get().scope;
	}
	
	@Override
	public boolean isGlobal() {
		return getCreator().equals("*");
	}

	@Override
	public boolean isSandbox() {
		return false;
	}

	@Override
	public int categoryIndex() {
		// Categorize by package - "somewhere in the middle",
		// but grouped with other components from the same package.
		return getClass().getPackage().getName().hashCode() & 0xFFFF;
	}

	@Override
	public int componentIndex() {
		return (getClass().getName().hashCode() << 1) + (isGlobal() ? 0 : 1);
	}
}
