package gov.nasa.arc.mct.qspersistence.service;

public class InternalPersistenceAccess {
	private static PersistenceServiceImpl persistence;
	protected static void setPersistenceService(PersistenceServiceImpl p) {
		persistence = p;
	}
	public static PersistenceServiceImpl getPersistenceService() {
		return persistence;
	}
}
