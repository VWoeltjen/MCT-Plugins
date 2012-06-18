package gov.nasa.arc.mct.labs.ancestorview.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfo;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfoImpl;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.roles.events.AddChildEvent;
import gov.nasa.arc.mct.roles.events.RemoveChildEvent;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.BorderLayout;

public class AncestorView extends View {
    private static final String VIEW_ROLE_NAME = "Ancestors";
    private static final ViewInfo VIEW_INFO = new ViewInfo(AncestorView.class, "Ancestor", ViewType.EMBEDDED);

    public AncestorView(AbstractComponent ac, ViewInfo vi)  {
        super(ac, vi);
        setLayout(new BorderLayout());
        add(RadialGraphView.createGraph(getManifestedComponent()), BorderLayout.CENTER);
    }

    @Override
    public void updateMonitoredGUI() {
    	if (getManifestedComponent().getWorkUnitDelegate() != null) {
    		// reload graph
    	}
    }

    @Override
    public void updateMonitoredGUI(AddChildEvent event) {
    	updateMonitoredGUI();
    }

    @Override
    public void updateMonitoredGUI(RemoveChildEvent event) {
    	updateMonitoredGUI();
    }


}
