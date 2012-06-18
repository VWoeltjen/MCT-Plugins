package gov.nasa.arc.mct.chronology.log.policy;

import gov.nasa.arc.mct.chronology.log.component.UserLogComponent;
import gov.nasa.arc.mct.chronology.log.view.NotebookView;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.chronology.log.component.UserLogEntryComponent;

public class UserLogFilterViewPolicy implements Policy {

	@Override
	public ExecutionResult execute(PolicyContext context) {
		boolean result = true;
		ViewInfo viewInfo = context.getProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), ViewInfo.class);

		if (viewInfo.getViewType().equals(ViewType.OBJECT) || 
			viewInfo.getViewType().equals(ViewType.CENTER) ||
			viewInfo.getViewType().equals(ViewType.EMBEDDED)) {
			AbstractComponent targetComponent = context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), AbstractComponent.class);
			boolean isUserLog = UserLogComponent.class.isAssignableFrom(targetComponent.getClass()) ||
			                    UserLogEntryComponent.class.isAssignableFrom(targetComponent.getClass());
			if (NotebookView.class.isAssignableFrom(viewInfo.getViewClass())) {
				result = isUserLog; 
			} else if (isUserLog) {
				if (viewInfo.getViewName().contains("Plot"))   result = false;
				if (viewInfo.getViewName().contains("Alpha"))  result = false;
				if (viewInfo.getViewName().contains("Canvas")) result = false;
			}
		}
		
		return new ExecutionResult(context, result, null);
	}

}
