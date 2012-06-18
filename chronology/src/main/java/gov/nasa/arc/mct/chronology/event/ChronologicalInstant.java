package gov.nasa.arc.mct.chronology.event;

import gov.nasa.arc.mct.chronology.ChronologyDomain;

public interface ChronologicalInstant {
	public ChronologyDomain<? extends ChronologicalInstant> getDomain();
}
