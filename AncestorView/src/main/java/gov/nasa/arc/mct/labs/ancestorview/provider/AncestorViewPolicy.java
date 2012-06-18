package gov.nasa.arc.mct.labs.ancestorview.provider;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.labs.ancestorview.view.AncestorView;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;


public class AncestorViewPolicy implements Policy {
	
	@Override
	public ExecutionResult execute(PolicyContext context) {
		
		boolean result = true;
		ViewInfo viewInfo = context.getProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), ViewInfo.class);
		if (AncestorView.class.isAssignableFrom(viewInfo.getViewClass())) {
			AbstractComponent targetComponent = context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), AbstractComponent.class);
			
			result = !targetComponent.getReferencingComponents().isEmpty();
		}
        return new ExecutionResult(context, result, "Component did not have ancestors");
	}
}
