package gov.nasa.arc.mct.chronology.log.component;

import gov.nasa.arc.mct.services.component.ComponentTypeInfo;

public class UserLogEntryComponent extends LogEntryComponent<String> {
	public static final ComponentTypeInfo COMPONENT_TYPE_INFO = 
		new ComponentTypeInfo("Notebook entry", "A time-stamped text entry into a notebook.", UserLogEntryComponent.class);
}
