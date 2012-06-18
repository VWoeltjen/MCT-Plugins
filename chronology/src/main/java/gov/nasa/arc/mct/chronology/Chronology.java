package gov.nasa.arc.mct.chronology;

import gov.nasa.arc.mct.chronology.event.ChronologicalEvent;
import gov.nasa.arc.mct.chronology.event.ChronologicalInstant;
import gov.nasa.arc.mct.chronology.event.ChronologicalInterval;

import java.util.List;

/**
 * A Chronology describes an ordered set of events.
 * 
 * A component that contains or manages a model which has some sequential nature may 
 * publish this information by offering a Chronology through getCapability(). 
 * 
 * @author vwoeltje
 *
 * @param <T> the type of object which would describe a point within this Chronology
 */
public interface Chronology<T extends ChronologicalInstant> {
	
	/**
	 * Get the domain in which the events reside - for instance, the UNIX time domain. 
	 * The domain is used to perform operations and comparisons involving events in a 
	 * generic way. 
	 *  
	 * @return the domain in which events of this Chronology exist
	 */
	public ChronologyDomain<T>         getDomain();
	
	/**
	 * Get all events contained within this chronology. 
	 * 
	 * @return a list of all events in the chronology
	 */
	public List<ChronologicalEvent<T>> getEvents();
	
	/**
	 * Get a list of events within a given interval. 
	 * 
	 * @param interval describes the start and end instants of interest for this request
	 * @return a list of all events within the interval
	 */
	public List<ChronologicalEvent<T>> getEvents(ChronologicalInterval<T> interval);
	
	/**
	 * Determine whether or not this Chronology may be changed within MCT
	 * (some Chronologies may represent read-only data) 
	 * @return true if the Chronology may be changed within MCT
	 */
	public boolean                     isMutable();

	/**
	 * Adds a new event to this Chronology. 
	 * Note that a Chronology behaves differently from most containers, in that a reference to the specific 
	 * event object added will most likely not be retained; rather 
	 * @param event the event to add to this Chronology
	 * @param start the start time (will override the event's own start & end)
	 * @return true if the event was successfully added; otherwise false
	 */
	public boolean add(ChronologicalEvent<T> event, T start);
	
	/**
	 * Remove an event from this Chronology.
	 * @param event the event to be removed.
	 * @return true if the event was successfully removed; otherwise false
	 */
	public boolean remove(ChronologicalEvent<T> event);
}
