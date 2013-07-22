package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;
import gov.nasa.arc.mct.scenario.util.DurationFormatter;

import java.text.ParseException;
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
		PropertyDescriptor type = new PropertyDescriptor("Activity Type",
				new TypePropertyEditor(this), VisualControlDescriptor.TextField);
		type.setFieldMutable(true);
		PropertyDescriptor startTime = new PropertyDescriptor("Start Time", 
				new ActivityStartTimePropertyEditor(this),  VisualControlDescriptor.TextField);
		startTime.setFieldMutable(true);
		PropertyDescriptor endTime = new PropertyDescriptor("End Time", 
				new ActivityEndTimePropertyEditor(this),  VisualControlDescriptor.TextField);
		endTime.setFieldMutable(true);
		PropertyDescriptor duration = new PropertyDescriptor("Duration",
				new DurationPropertyEditor(this), VisualControlDescriptor.TextField);
		duration.setFieldMutable(true);
		PropertyDescriptor power = new PropertyDescriptor("Power (W)", 
				new PowerPropertyEditor(this),  VisualControlDescriptor.TextField);
		power.setFieldMutable(true);
		PropertyDescriptor comm = new PropertyDescriptor("Comm (Kb/s)", 
				new CommPropertyEditor(this),  VisualControlDescriptor.TextField);
		comm.setFieldMutable(true);

		fields.add(type);
		fields.add(startTime);
		fields.add(endTime);
		fields.add(duration);
		fields.add(power);
		fields.add(comm);

		return fields;
	}
	
	public String getType()
	{
		return getData().getActivityType();
	}

	@Override
	public long getStart() {
		return getData().getStartTime().getTime();
	}

	@Override
	public long getEnd() {
		return getData().getEndTime().getTime();
	}
	
	public long getDuration() {
		return getData().getDurationTime();
	}
	
	public void setType(String type)
	{
		getData().setActivityType(type);
		save();
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
	
	public void setDuration(long duration) {
		getData().setDurationTime(duration);
		save();
	}

	public void constrainChildren(DurationCapability source, boolean isStart) {
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
			constrainChildren(durationCapabilityToShift, isStart);
		}
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