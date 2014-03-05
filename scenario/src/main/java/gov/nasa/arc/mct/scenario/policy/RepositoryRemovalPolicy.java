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

/**
 * Policy preventing removal of objects from object repositories.
 * 
 * Any repository-bound object must exist in exactly one repository, 
 * so there is no valid usage of Remove Manifestation when the object 
 * being removed from is a repository.
 */
public class RepositoryRemovalPolicy implements Policy {

	@Override
	public ExecutionResult execute(PolicyContext context) {
		AbstractComponent parentComponent = context.getProperty(
				PolicyContext.PropertyName.TARGET_COMPONENT.getName(), AbstractComponent.class);

		return new ExecutionResult(context, 
				parentComponent == null || 
				parentComponent.getCapability(RepositoryCapability.class) == null || 
				"Move".equals(context.getProperty("DRAG_DROP_ACTION_TYPE", String.class)), 
				"Cannot remove objects from repository (can only delete)");
	}
}
