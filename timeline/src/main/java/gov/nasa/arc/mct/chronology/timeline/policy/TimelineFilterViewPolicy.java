package gov.nasa.arc.mct.chronology.timeline.policy;

import gov.nasa.arc.mct.chronology.timeline.component.TimelineComponent;
import gov.nasa.arc.mct.chronology.timeline.view.TimelineView;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.chronology.Chronology;

/**
 * Determines which components can have Timeline Views.  
 * 
 * @author vwoeltje
 *
 */
public class TimelineFilterViewPolicy implements Policy {

	@Override
	public ExecutionResult execute(PolicyContext context) {
		ExecutionResult trueResult = new ExecutionResult(context, true, "");
		
		AbstractComponent comp = context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), AbstractComponent.class);
		ViewInfo viewInfo = context.getProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), ViewInfo.class);
		
		// Only intervene in the policy decision for Timeline Views
		if (TimelineView.class.isAssignableFrom(viewInfo.getViewClass())) {
			// Timeline Components always have Timeline views
			if (TimelineComponent.class.isAssignableFrom(comp.getClass())) return trueResult;
			
			// Otherwise, look for Chronological data in either the component or its immediate children
			if (comp.getCapability(Chronology.class) != null) return trueResult;
			for (AbstractComponent child : comp.getComponents())
				if (child.getCapability(Chronology.class) != null) return trueResult;
			
			return new ExecutionResult (context, false, null);
		}
		
		return trueResult;
	}

}
