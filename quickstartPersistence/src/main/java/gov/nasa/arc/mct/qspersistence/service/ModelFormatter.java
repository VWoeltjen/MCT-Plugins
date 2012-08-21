package gov.nasa.arc.mct.qspersistence.service;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

import java.util.HashMap;
import java.util.Map;

public class ModelFormatter {
	private static final Map<String, String> formats = new HashMap<String, String>();

	static {
	formats.put("gov.nasa.arc.mct.chronology.log.component.UserLogEntryComponent",
			"<logEntry><creationTime>%s</creationTime><entry xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"" +
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">%s</entry></logEntry>");

	formats.put("gov.nasa.arc.mct.chronology.log.component.UserLogEntryComponent",
					"<logEntry><creationTime>%s</creationTime><entry xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"" +
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">%s</entry><references>%s</references></logEntry>");
						
	formats.put("gov.nasa.arc.mct.earth.component.CoordinateComponent",
			"<coordinateModel><velocity>%s</velocity><axis>%s</axis><parent>%s</parent></coordinateModel>");

	formats.put   ("gov.nasa.arc.mct.earth.component.UserOrbitalComponent",
			"<orbitalModel><initialPosition><x>%s</x><y>%s</y><z>%s</z></initialPosition><initialVelocity><x>%s</x><y>%s</y><z>%s</z></initialVelocity><initialTime>" + System.currentTimeMillis() + "</initialTime></orbitalModel>");
	}

	public static void applyModel(AbstractComponent comp, Object... args) {
		String format = formats.get(comp.getClass().getName());
		String model  = String.format(format, args);
		comp.getCapability(ModelStatePersistence.class).setModelState(model);
	}
	
}
