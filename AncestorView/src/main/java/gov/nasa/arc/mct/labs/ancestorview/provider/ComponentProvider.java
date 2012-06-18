package gov.nasa.arc.mct.labs.ancestorview.provider;

import gov.nasa.arc.mct.labs.ancestorview.view.AncestorView;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Collection;
import java.util.Collections;

public class ComponentProvider extends AbstractComponentProvider {

	private final Collection<ViewInfo> views;
	private final Collection<PolicyInfo> policyInfos;
    
    public ComponentProvider() {
        views = Collections.<ViewInfo>singleton(
        		new ViewInfo(AncestorView.class, "Ancestor View", ViewType.OBJECT) );
        policyInfos = Collections.singletonList(
		           new PolicyInfo(PolicyInfo.CategoryType.FILTER_VIEW_ROLE.getKey(),
				   AncestorViewPolicy.class));
    }
    

	@Override
	public Collection<PolicyInfo> getPolicyInfos() {
		return policyInfos;
	}

    @Override
	public Collection<ViewInfo> getViews(String componentTypeId) {
		return views;
	}



}
