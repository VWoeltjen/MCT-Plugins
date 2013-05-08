package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.scenario.api.ActivityCreationService;
import gov.nasa.arc.mct.scenario.component.ActivityComponent;
import gov.nasa.arc.mct.scenario.component.ActivityCreationServiceImpl;
import gov.nasa.arc.mct.services.component.ComponentRegistry;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

public class ActivityCreationServiceImpl implements ActivityCreationService {

	private final static AtomicReference<ComponentRegistry> registry = new AtomicReference<ComponentRegistry>();
	
	public void setComponentRegistry(ComponentRegistry registry) {
		ActivityCreationServiceImpl.registry.set(registry);
	}
	
	public void removeComponentRegistry(ComponentRegistry registry) {
		ActivityCreationServiceImpl.registry.set(null);
	}
	
	@Override
	public AbstractComponent createActivity(
	                                         AbstractComponent parent) {
		ComponentRegistry registry = ActivityCreationServiceImpl.registry.get();
		ActivityComponent activity = 
		                          registry.newInstance(ActivityComponent.class, parent);
		activity.getModel().getData().setStartDate(Calendar.getInstance().getTime());
		activity.getModel().getData().setStartDate(Calendar.getInstance().getTime());
		activity.getModel().getData().setPower(Double.NaN);
		activity.getModel().getData().setComm(Double.NaN);
		activity.save();
		
		return activity;
	}
}
