package gov.nasa.arc.mct.nontimeplot;

import gov.nasa.arc.mct.nontimeplot.view.NonTimePlotView;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Collection;
import java.util.Collections;

public class NonTimePlotComponentProvider extends AbstractComponentProvider {

	/* (non-Javadoc)
	 * @see gov.nasa.arc.mct.services.component.AbstractComponentProvider#getViews(java.lang.String)
	 */
	@Override
	public Collection<ViewInfo> getViews(String componentTypeId) {
		return Collections.singleton(new ViewInfo(NonTimePlotView.class, "Non Time", ViewType.OBJECT));
	}

}
