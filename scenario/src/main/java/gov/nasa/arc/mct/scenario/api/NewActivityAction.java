package gov.nasa.arc.mct.scenario.api;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.DetectGraphicsDevices;
import gov.nasa.arc.mct.scenario.component.ActivityCreationServiceImpl;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.View;

public class NewActivityAction extends ContextAwareAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2933386263765070857L;
	private static final Logger logger = LoggerFactory.getLogger(NewActivityAction.class);
	private Collection<View> selectedManifestations;
	private static ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
    
    private String graphicsDeviceName;

	/**
	 * Place objects in enum action constructor.
	 */
    public NewActivityAction() {
        super("Create new activity...");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Set<AbstractComponent> sourceComponents = new LinkedHashSet<AbstractComponent>();
        for (View manifestation : selectedManifestations)
            sourceComponents.add(manifestation.getManifestedComponent());
        
        final AbstractComponent activity = createNewActivity(sourceComponents);
        	activity.setDisplayName("Activity 1");
        	// invoke later so that transaction will have already completed
        	SwingUtilities.invokeLater(new Runnable() {
        		public void run() {
        			openNewActivity("Activity 1", activity);
        		}
        	});
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    /**
     * Creates a new component based on the collection of source components.
     * @param sourceComponents the collection of components.
     * @return newly created abstract component.
     */
    AbstractComponent createNewActivity(Collection<AbstractComponent> sourceComponents) {
    	ActivityCreationServiceImpl e = new ActivityCreationServiceImpl();
    	AbstractComponent ac = sourceComponents.iterator().next();
    	AbstractComponent activity = e.createActivity(ac);
    	ac.addDelegateComponents(Collections.singleton(activity));
    	return ac;
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        
    	if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() > DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) {       
            
    		if (context.getWindowManifestation() != null) {
    			graphicsDeviceName = getActiveGraphicsDeviceName(context.getWindowManifestation());
    		} 
        }
    	
    	selectedManifestations = context.getSelectedManifestations();
        if (selectedManifestations.isEmpty()){
            //No objects selected to add to a new Enumerator
            return false;
        }
        return true;
    }
    
    private String getActiveGraphicsDeviceName(Component component) {
    	String deviceName = null;
    	JFrame rootJFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, component);
 		 
 	 	if (rootJFrame != null) {	            
 	 		deviceName = rootJFrame.getGraphicsConfiguration().getDevice().getIDstring();
 	 	 	if (deviceName != null) {
 	 	 		deviceName = deviceName.replace("\\", "");
 	 	 	}
 	 	} else {
 	 	 		logger.warn("Cannot get root JFrame from SwingUtilities.getAncestorOfClass() for multi-monitor support.");
 	 	}
    	return deviceName;
    }
    
    /**
     * Opens the new enum.
     * @param name display name.
     * @param e component.
     */
    void openNewActivity(String name, AbstractComponent e) {
        if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() > DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) {
            GraphicsConfiguration graphicsConfig = DetectGraphicsDevices.getInstance().getSingleGraphicDeviceConfig(graphicsDeviceName);
            e.open(graphicsConfig);
        } else {
            e.open();
        }
    }
}
