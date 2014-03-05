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
package gov.nasa.arc.mct.scenario.policy;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.scenario.component.RepositoryCapability;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Policy preventing drag-drop linking of objects between 
 * repositories.
 * 
 * Any repository-bound object must exist in exactly one repository, 
 * so a Link when dropping into a repository must be disallowed.
 */
public class RepositoryLinkPolicy implements Policy {

	@Override
	public ExecutionResult execute(PolicyContext context) {
		AbstractComponent parentComponent = context.getProperty(
				PolicyContext.PropertyName.TARGET_COMPONENT.getName(), AbstractComponent.class);
		Collection<?> childComponents = context.getProperty(
				PolicyContext.PropertyName.SOURCE_COMPONENTS.getName(), Collection.class);
		
		return new ExecutionResult(context, 
				parentComponent == null || 
				parentComponent.getCapability(RepositoryCapability.class) == null || 
				!"Copy Linked".equals(context.getProperty("DRAG_DROP_ACTION_TYPE", String.class)) || 
				alreadyContains(parentComponent, (Collection<?>) childComponents), 
				"Cannot remove objects from repository (can only delete)");
	}
	
	private boolean alreadyContains(AbstractComponent parentComponent, Collection<?> childComponents) {
		Set<String> contained = new HashSet<String>();
		for (AbstractComponent child : parentComponent.getComponents()) {
			contained.add(child.getComponentId());
		}
		for (Object o : childComponents) {
			if (o instanceof AbstractComponent) {
				if (!contained.contains(((AbstractComponent) o).getComponentId())) {
					return false;
				}
			}
		}
		return true;
	}
}
