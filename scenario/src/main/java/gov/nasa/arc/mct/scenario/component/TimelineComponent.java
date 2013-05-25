package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.HashSet;
import java.util.Set;

public class TimelineComponent extends AbstractComponent {
	
	public String getDisplay(){
		return this.getDisplayName();
	}

	private Set<AbstractComponent> modifiedObjects = new HashSet<AbstractComponent>();
	public void addToModifiedObjects(AbstractComponent ac) {
		modifiedObjects.add(ac);
	}
	@Override
	public Set<AbstractComponent> getAllModifiedObjects() {
		return modifiedObjects;
	}
	
	@Override
	public void notifiedSaveAllSuccessful() {
		modifiedObjects.clear();
	}
}