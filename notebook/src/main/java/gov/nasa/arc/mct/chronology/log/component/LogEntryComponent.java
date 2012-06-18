package gov.nasa.arc.mct.chronology.log.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LogEntryComponent<T> extends AbstractComponent {
	private AtomicReference<LogEntry<T>> model = new AtomicReference<LogEntry<T>>(new LogEntry<T>());
	
	public void initialize(T logEntry, List<AbstractComponent> components) {
		model.set(new LogEntry<T>(logEntry, components));
		addDelegateComponents(components);
	}
	
	public LogEntry<T> getEntry() {
		return model.get();
	}
	
	protected <C> C handleGetCapability(Class<C> capability) {
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
		    JAXBModelStatePersistence<LogEntry<T>> persistence = new JAXBModelStatePersistence<LogEntry<T>>() {

				@Override
				protected LogEntry<T> getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(LogEntry<T> modelState) {
					model.set(modelState);
				}

				@SuppressWarnings("unchecked")
				@Override
				protected Class<LogEntry<T>> getJAXBClass() {
					return (Class<LogEntry<T>>) model.get().getClass();
				}
		        
			};
			
			return capability.cast(persistence);
		}
		

		
		return null;
	}
}
