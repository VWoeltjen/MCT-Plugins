package gov.nasa.arc.mct.quickstart;

import gov.nasa.arc.mct.platform.Startup;

public class Quickstart {
	public static void main (String[] args) {
		if (System.getProperty("rwRoot") == null) {
			System.setProperty("rwRoot", ".");
		}
		System.setProperty("mct.db.check-schema-version", "false");
		System.setProperty("disableShiftChangeMonitor",   "true");
		System.setProperty("mct.user",                    "jimbooster");
		Startup.main(args);
	}
}
