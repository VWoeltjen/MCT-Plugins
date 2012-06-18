package gov.nasa.arc.mct.chronology.timeline.policy;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.chronology.timeline.view.TimelineView;

public class TimelinePreferredViewPolicy implements Policy {

	@Override
	public ExecutionResult execute(PolicyContext context) {
		ExecutionResult trueResult = new ExecutionResult(context, true, "");
		
		AbstractComponent comp = context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), AbstractComponent.class);
		ViewInfo viewInfo = context.getProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), ViewInfo.class);
		
		if (TimelineView.class.equals(viewInfo.getViewClass())) {

		} 
		return trueResult;
	}

}
