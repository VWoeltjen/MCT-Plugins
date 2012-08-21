package gov.nasa.arc.mct.qspersistence.service;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

public enum ComponentFactory {

	LOG_ENTRY("gov.nasa.arc.mct.chronology.log.component.UserLogEntryComponent",
			"<logEntry><creationTime>%s</creationTime><entry xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"" +
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">%s</entry></logEntry>"),

	REFERENCING_LOG_ENTRY("gov.nasa.arc.mct.chronology.log.component.UserLogEntryComponent",
					"<logEntry><creationTime>%s</creationTime><entry xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"" +
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">%s</entry><references>%s</references></logEntry>"),
						
	COORDINATE("gov.nasa.arc.mct.earth.component.CoordinateComponent",
			"<coordinateModel><velocity>%s</velocity><axis>%s</axis><parent>%s</parent></coordinateModel>"),

	ORBITAL   ("gov.nasa.arc.mct.earth.component.UserOrbitalComponent",
			"<orbitalModel><initialPosition><x>%s</x><y>%s</y><z>%s</z></initialPosition><initialVelocity><x>%s</x><y>%s</y><z>%s</z></initialVelocity><initialTime>" + System.currentTimeMillis() + "</initialTime></orbitalModel>");

	
	private String className;
	private String modelFormat;
	
	private ComponentFactory(String className, String modelFormat) {
		this.className = className;
		this.modelFormat = modelFormat;
	}
	
	public AbstractComponent getComponent(Object... args) {
		// TODO: If args.length != count of %s's, throw exception?		
		AbstractComponent comp = PlatformAccess.getPlatform().getComponentRegistry().newInstance(className);
		comp.getCapability(ModelStatePersistence.class).setModelState(String.format(modelFormat, args));
		return comp;
	}
	
	public static AbstractComponent makeLogEntry(long time, String entry) {
		return LOG_ENTRY.getComponent(time, entry);
	}
	
	public static AbstractComponent makeLogEntry(long time, String entry, AbstractComponent reference) {
		return LOG_ENTRY.getComponent(time, entry, reference.getComponentId());
	}
	
	public static AbstractComponent makeCoordinate(boolean velocity, int axis, AbstractComponent parent) {
		return COORDINATE.getComponent(velocity, axis, parent.getComponentId());
	}
	
	public static AbstractComponent makeOrbital(float x, float y, float z, float vx, float vy, float vz) {
		return ORBITAL.getComponent(x, y, z, vx, vy, vz);
	}
}
