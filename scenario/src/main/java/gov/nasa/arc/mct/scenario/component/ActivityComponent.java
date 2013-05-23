package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ActivityComponent extends AbstractComponent {
	private final AtomicReference<ActivityModelRole> model = new AtomicReference<ActivityModelRole>(new ActivityModelRole());
	
	public ActivityData getData() {
		return getModel().getData();
	}
	
	public String getDisplay(){
		return this.getDisplayName();
	}
	
	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (ActivityComponent.class.isAssignableFrom(capability)) {
			return capability.cast(this);
		}
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
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



}