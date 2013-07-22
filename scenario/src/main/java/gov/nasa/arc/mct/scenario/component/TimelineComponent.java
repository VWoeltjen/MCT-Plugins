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

/**
 * A Timeline serves as a container for activities. Multiple timelines may be arranged 
 * within a Scenario.
 * 
 *
 */
public class TimelineComponent extends CostFunctionComponent implements DurationCapability {
	
	public String getDisplay(){
		return this.getDisplayName();
	}

	private Set<AbstractComponent> modifiedObjects = new HashSet<AbstractComponent>();
	
	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (capability.isAssignableFrom(getClass())) {
			return capability.cast(this);
		}
		return super.handleGetCapability(capability);
	}
	
	public void addToModifiedObjects(AbstractComponent ac) {
		modifiedObjects.add(ac);
	}
	@Override
	public Set<AbstractComponent> getAllModifiedObjects() {
		// TODO: What about cycles?
		Set<AbstractComponent> modified = new HashSet<AbstractComponent>();
		for (AbstractComponent child : getComponents()) {
			if (child.isDirty()) {
				modified.add(child);
			}
			modified.addAll(child.getAllModifiedObjects());
		}
		return modified;
	}
	
	@Override
	public void notifiedSaveAllSuccessful() {
		modifiedObjects.clear();
	}
	
	@Override
	public long getStart() {		
		return 0;
	}
	@Override
	public long getEnd() {
		// The end of a Timeline is the end of its last activity
		long end = 0;
		for (AbstractComponent child : getComponents()) {
			DurationCapability dc = child.getCapability(DurationCapability.class);
			if (dc != null) {
				long childEnd = dc.getEnd();
				if (childEnd > end) {
					end = childEnd;
				}
			}
		}
		return end;
	}
	@Override
	public void setStart(long start) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setEnd(long end) {
		// TODO Auto-generated method stub
		
	}
}