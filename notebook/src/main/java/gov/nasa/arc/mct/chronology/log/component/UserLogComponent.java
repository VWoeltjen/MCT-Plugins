package gov.nasa.arc.mct.chronology.log.component;



public class UserLogComponent extends LogComponent<String> {
	@Override
	public Class<? extends LogEntryComponent<String>> getEntryComponentClass() {
		return UserLogEntryComponent.class;
	}
}

