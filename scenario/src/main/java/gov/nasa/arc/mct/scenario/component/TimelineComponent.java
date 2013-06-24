package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TimelineComponent extends CostFunctionComponent {
	
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