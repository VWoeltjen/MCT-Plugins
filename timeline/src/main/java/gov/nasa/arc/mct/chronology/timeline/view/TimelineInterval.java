package gov.nasa.arc.mct.chronology.timeline.view;

import gov.nasa.arc.mct.chronology.ChronologyDomain;
import gov.nasa.arc.mct.chronology.event.ChronologicalInstant;
import gov.nasa.arc.mct.chronology.event.ChronologicalInterval;

/**
 * Describes a pair of intervals - a visible interval and a larger off-screen interval - along 
 * with the time domain to which they both belong. Encapsulating all within one interface helps 
 * to coordinate type safety among generics.
 * @author vwoeltje
 *
 * @param <T> an object representing an instant within the relevant time domain
 */
public interface TimelineInterval<T extends ChronologicalInstant> {
	/**
	 * Get the domain in which this interval is described.
	 * @return the domain in which this interval is described 
	 */
	public ChronologyDomain<T>      getDomain();
	
	/**
	 * Get the interval (start and end coordinates) which is currently designated for display.
	 * @return the interval which is to be displayed
	 */
	public ChronologicalInterval<T> getInterval();
	
	/**
	 * Get the boundary interval for this timeline - the area which is available for display, 
	 * which may extend off-screen.
	 * @return the interval which is available to be displayed
	 */
	public ChronologicalInterval<T> getBoundary();
}
