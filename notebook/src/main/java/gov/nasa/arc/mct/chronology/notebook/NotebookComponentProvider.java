package gov.nasa.arc.mct.chronology.notebook;

import gov.nasa.arc.mct.chronology.log.component.UserLogComponent;
import gov.nasa.arc.mct.chronology.log.policy.UserLogFilterViewPolicy;
import gov.nasa.arc.mct.chronology.log.view.NotebookView;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import gov.nasa.arc.mct.chronology.log.component.UserLogEntryComponent;

public class NotebookComponentProvider extends AbstractComponentProvider {
	private static final Collection<ComponentTypeInfo> COMPONENT_TYPES = Arrays.asList(
			new ComponentTypeInfo("Notebook", "A user-entered series of events", UserLogComponent.class, true),
			new ComponentTypeInfo("Notebook Entry", "An entry in a notebook", UserLogEntryComponent.class, false)
			); 

    private static final List<ViewInfo> VIEW_INFOS = Arrays.asList(
    		new ViewInfo(NotebookView.class, NotebookView.VIEW_ROLE_NAME, ViewType.OBJECT),
    		new ViewInfo(NotebookView.class, NotebookView.VIEW_ROLE_NAME, ViewType.EMBEDDED),
    		new ViewInfo(NotebookView.class, NotebookView.VIEW_ROLE_NAME, ViewType.CENTER)
    		);  
	
    private static final Collection<PolicyInfo> POLICY_INFOS = Arrays.asList(
    		new PolicyInfo(PolicyInfo.CategoryType.FILTER_VIEW_ROLE
                    .getKey(), UserLogFilterViewPolicy.class)
    		);
    
	@Override
	public Collection<ComponentTypeInfo> getComponentTypes() {
		return COMPONENT_TYPES;
	}

	@Override
	public Collection<ViewInfo> getViews(String componentTypeId) {
		return VIEW_INFOS;
	}

	@Override
	public Collection<PolicyInfo> getPolicyInfos() {
		return POLICY_INFOS;
	}

	
}
