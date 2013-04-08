package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.chronology.Chronology;
import gov.nasa.arc.mct.chronology.ChronologyDomain;
import gov.nasa.arc.mct.chronology.event.ChronologicalEvent;
import gov.nasa.arc.mct.chronology.event.ChronologicalInterval;
import gov.nasa.arc.mct.chronology.event.UNIXTimeInstant;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DecisionComponent extends AbstractComponent implements Chronology<UNIXTimeInstant>{
	private final AtomicReference<ActivityModelRole> model = new AtomicReference<ActivityModelRole>(new ActivityModelRole());
	
	public ActivityData getData() {
		return getModel().getData();
	}
	
	public String getDisplay(){
		return this.getDisplayName();
	}
	
	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (DecisionComponent.class.isAssignableFrom(capability)) {
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
		if (Chronology.class.isAssignableFrom(capability)) {
			return capability.cast(this);
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
		PropertyDescriptor duration = new PropertyDescriptor("Duration: ", 
				new TextPropertyEditor(this),  VisualControlDescriptor.TextField);
		duration.setFieldMutable(true);


		fields.add(duration);

		return fields;
	}

	@Override
	public ChronologyDomain<UNIXTimeInstant> getDomain() {
		return UNIXTimeInstant.DOMAIN;
	}

	@Override
	public List<ChronologicalEvent<UNIXTimeInstant>> getEvents() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public List<ChronologicalEvent<UNIXTimeInstant>> getEvents(
			ChronologicalInterval<UNIXTimeInstant> interval) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add(ChronologicalEvent<UNIXTimeInstant> event,
			UNIXTimeInstant start) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(ChronologicalEvent<UNIXTimeInstant> event) {
		// TODO Auto-generated method stub
		return false;
	}

}