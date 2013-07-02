package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ActivityComponent extends CostFunctionComponent implements DurationCapability {
	private final AtomicReference<ActivityModelRole> model = new AtomicReference<ActivityModelRole>(new ActivityModelRole());
	
	public ActivityData getData() {
		return getModel().getData();
	}
	
	public String getDisplay(){
		return this.getDisplayName();
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
	protected <T> T handleGetCapability(Class<T> capability) {
		if (capability.isAssignableFrom(getClass())) {
			return capability.cast(this);
		}
		if (capability.isAssignableFrom(ModelStatePersistence.class)) {
		    JAXBModelStatePersistence<ActivityModelRole> persistence = new JAXBModelStatePersistence<ActivityModelRole>() {

				@Override
				protected ActivityModelRole getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(ActivityModelRole modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<ActivityModelRole> getJAXBClass() {
					return ActivityModelRole.class;
				}
		        
			};
			
			return capability.cast(persistence);
		}
		return null;
	}
	
	@Override
	public List<CostFunctionCapability> getInternalCostFunctions() {
		return Arrays.<CostFunctionCapability>asList(new CostFunctionStub(true), new CostFunctionStub(false));
	}

	public ActivityModelRole getModel() {
		return model.get();
	}

	@Override
	public List<PropertyDescriptor> getFieldDescriptors()  {

		// Provide an ordered list of fields to be included in the MCT Platform's InfoView.
		List<PropertyDescriptor> fields = new ArrayList<PropertyDescriptor>();


		// Describe MyData's field "doubleData". 
		// We specify a mutable text field.  The control display's values are maintained in the business model
		// via the PropertyEditor object.  When a new value is to be set, the editor also validates the prospective value.
		PropertyDescriptor startTime = new PropertyDescriptor("Start Time", 
				new ActivityStartTimePropertyEditor(this),  VisualControlDescriptor.TextField);
		startTime.setFieldMutable(true);
		PropertyDescriptor endTime = new PropertyDescriptor("End Time", 
				new ActivityEndTimePropertyEditor(this),  VisualControlDescriptor.TextField);
		endTime.setFieldMutable(true);
		PropertyDescriptor power = new PropertyDescriptor("Power (W)", 
				new PowerPropertyEditor(this),  VisualControlDescriptor.TextField);
		power.setFieldMutable(true);
		PropertyDescriptor comm = new PropertyDescriptor("Comm (Kb/s)", 
				new CommPropertyEditor(this),  VisualControlDescriptor.TextField);
		comm.setFieldMutable(true);

		fields.add(startTime);
		fields.add(endTime);
		fields.add(power);
		fields.add(comm);

		return fields;
	}

	@Override
	public long getStart() {
		return getData().getStartTime().getTime();
	}

	@Override
	public long getEnd() {
		return getData().getEndTime().getTime();
	}

	@Override
	public void setStart(long start) {
		getData().setStartDate(new Date(start));
		save();		
	}

	@Override
	public void setEnd(long end) {
		getData().setEndDate(new Date(end));
		save();
	}

	public void constrainChildren(DurationCapability source, boolean isStart) {
		constrainActivities(source, isStart);
		constrainToDuration();
		constrainDecisions(isStart);
	}
	
	private void constrainToDuration() {
		long minimum = getStart();
		long maximum = getEnd();
		DurationCapability latest = null;
		DurationCapability earliest = null;
		for (AbstractComponent child : getComponents()) {
			DurationCapability dc = child.getCapability(DurationCapability.class);
			if (dc != null) {
				if (dc.getStart() < minimum) {
					earliest = dc;
					minimum = dc.getStart();
				}
				if (dc.getEnd() > maximum) {
					latest = dc;
					maximum = dc.getEnd();
				}
			}
		}
		if (maximum > getEnd() && minimum < getStart()) {
			
		} else {
			long delta = 0L;
			if (maximum > getEnd()) {
				delta = getEnd() - maximum;
				latest.setEnd(getEnd());
				latest.setStart(latest.getStart() + delta);
				constrainActivities(latest, true);
			} 
			if (minimum < getStart()) {
				delta = getStart() - minimum;
				earliest.setEnd(earliest.getEnd() + delta);
				earliest.setStart(getStart());
				constrainActivities(earliest, false);
			}
		}
	}
	
	private void constrainActivities(DurationCapability source, boolean isStart) {
		int sign = isStart ? 1 : -1;
		long movingEdge = isStart ? source.getStart() : source.getEnd();
		long mostOverlapping = movingEdge;
		DurationCapability durationCapabilityToShift = null;
		for (AbstractComponent child : getComponents()) {
			DurationCapability dc = child
					.getCapability(DurationCapability.class);
			if (dc != source && overlaps(dc, source)) {
				long movedEdge = isStart ? dc.getEnd() : dc.getStart();
				if (movedEdge * sign > mostOverlapping * sign) {
					mostOverlapping = movedEdge;
					durationCapabilityToShift = dc;
				}
			}
		}
		if (durationCapabilityToShift != null) {
			long delta = movingEdge - mostOverlapping;
			durationCapabilityToShift.setStart(
					durationCapabilityToShift.getStart() + delta);
			durationCapabilityToShift.setEnd(
					durationCapabilityToShift.getEnd() + delta);
			constrainActivities(durationCapabilityToShift, isStart);
		}
	}
	
	private void constrainDecisions(boolean movingTowardStart) {
		// Enforce special positioning rules for Decisions
		boolean moved = false;
		do {
			moved = false;
			for (AbstractComponent child : getComponents()) {
				if (child instanceof DecisionComponent) {
					long start = ((DecisionComponent) child).getStart();
					long end = ((DecisionComponent) child).getEnd();
					DurationCapability preceedingCapability = null;
					DurationCapability followingCapability = null;
					long nearestPrecedent = getStart();
					long nearestFollower = getEnd();
					for (AbstractComponent otherChild : getComponents()) {
						if (child != otherChild) {
							DurationCapability dc = otherChild
									.getCapability(DurationCapability.class);
							if (dc.getEnd() > nearestPrecedent && dc.getEnd() <= end) {
								preceedingCapability = dc;
								nearestPrecedent = dc.getEnd();
							}
							if (dc.getStart() < nearestFollower && dc.getStart() >= start) {
								 followingCapability = dc;
								 nearestFollower = dc.getStart();
							}
						}
					}
					if (preceedingCapability != null && nearestPrecedent < start && !overlaps((DurationCapability) child, preceedingCapability)) {
						long delta = (start - nearestPrecedent) * (movingTowardStart ? -1 : 1);
						DurationCapability toMove = (DurationCapability) (movingTowardStart ? child : preceedingCapability);
						toMove.setStart(toMove.getStart() + delta);
						toMove.setEnd(toMove.getEnd() + delta);
						moved = true;
					}
					if (followingCapability != null && nearestFollower > end && !overlaps((DurationCapability) child, followingCapability)) {
						long delta = (end - nearestFollower) * (movingTowardStart ? 1 : -1);
						DurationCapability toMove = (DurationCapability) (!movingTowardStart ? child : followingCapability);
						toMove.setStart(toMove.getStart() + delta);
						toMove.setEnd(toMove.getEnd() + delta);
						moved = true;
					}
				}
			}
		} while (moved);
	}
	
	/**
	 * Utility method to determine if two DurationCapabilities overlap 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean overlaps(DurationCapability a, DurationCapability b) {
		return (a.getStart() < b.getEnd() && a.getEnd() > b.getStart());
	}
	
	private class CostFunctionStub implements CostFunctionCapability {
		private boolean isComm; //Otherwise, is power
		
		public CostFunctionStub(boolean isComm) {
			super();
			this.isComm = isComm;
		}

		@Override
		public String getName() {
			return isComm ? "Comms" : "Power";
		}

		@Override
		public String getUnits() {
			return isComm ? "KB/s" : "Watts";
		}

		@Override
		public double getValue(long time) {
			if (time < getStart() || time >= getEnd()) {
				return 0;
			} else {
				return isComm ? getData().getComm() : getData().getPower();
			}
		}

		@Override
		public Collection<Long> getChangeTimes() {
			return Arrays.asList(getStart(), getEnd());
		}
		
	}


}