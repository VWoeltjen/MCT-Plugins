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
package org.acme.example.policy;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import org.acme.example.component.ExampleComponent;
import org.acme.example.view.PrivateInfoView;
import org.acme.example.view.PublicInfoView;

/**
 * The <code>FilterViewPolicy</code> filters out certain {@link ViewInfo}s 
 * based on a defined criteria. The criteria in in this <code>Policy</code> 
 * is defined in {@link FilterViewPolicy#checkVisibility(AbstractComponent)}.
 * 
 */
public class FilterViewPolicy implements Policy {
	
	@Override
	public ExecutionResult execute(PolicyContext context) {
		ExecutionResult trueResult = new ExecutionResult(context, true, "");

		if (!checkArguments(context))
			return trueResult; // Return true to skip this policy and pass the context to the next one.
		
		ExampleComponent targetComponent = context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), ExampleComponent.class);
		ViewInfo viewInfo = context.getProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), ViewInfo.class);
		if (viewInfo.getViewClass().equals(PublicInfoView.class))
			return new ExecutionResult(context, false, targetComponent.getDisplayName() 
								+ " is private, cannot show " + viewInfo.getClass().getName()); //NOI18N
		
		if (viewInfo.getViewClass().equals(PrivateInfoView.class))
			return new ExecutionResult(context, false, targetComponent.getDisplayName() 
								+ " is public, cannot show " + viewInfo.getClass().getName()); //NOI18N
		
		return trueResult;
	}
	
	/**
	 * This a utility method that checks if <code>context</code> 
	 * contains the correct set of arguments to process this policy.
	 * @param context the <code>PolicyContext</code>
	 * @return true if the <code>context</code> contains the correct arguments; otherwise return false
	 */
	private boolean checkArguments(PolicyContext context) {
		if (context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), ExampleComponent.class) == null)
			return false;
				
		// Filtering only InspectableViewRole types
		return !(context.getProperty(PolicyContext.PropertyName.VIEW_TYPE.getName(), ViewType.class) != ViewType.OBJECT);
	}

}
