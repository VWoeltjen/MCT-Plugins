package gov.nasa.arc.mct.labs.ancestorview.provider;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.OptionBox;

public class AboutAncestorViewAction extends ContextAwareAction {
	private static final long serialVersionUID = -7812963524721329849L;

	private static ResourceBundle bundle = ResourceBundle.getBundle("AncestorViewBundle"); 
	
	public AboutAncestorViewAction() {
		super(bundle.getString("about.title"));
	}

	@Override
	public boolean canHandle(ActionContext context) {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        OptionBox.showMessageDialog((Component) e.getSource(), bundle.getString("about.message"));
	}

}
