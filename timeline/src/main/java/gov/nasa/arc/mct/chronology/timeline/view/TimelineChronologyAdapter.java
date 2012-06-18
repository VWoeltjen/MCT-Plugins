package gov.nasa.arc.mct.chronology.timeline.view;

import gov.nasa.arc.mct.chronology.Chronology;
import gov.nasa.arc.mct.chronology.ChronologyDomain;
import gov.nasa.arc.mct.chronology.event.ChronologicalEvent;
import gov.nasa.arc.mct.chronology.event.ChronologicalInstant;
import gov.nasa.arc.mct.chronology.event.ChronologicalInterval;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * Serves as a proxy which maps events from one chronological domain to another
 * (for instance, may be used to convert outline steps - "1, 1.1, 1.2, 2, 2.1" - 
 * to the UNIX time domain). This conversion is linear.
 * @author vwoeltje
 *
 * @param <T> the class of object which represents a moment in the output domain
 * @param <S> the class of object which represents a moment in the input domain
 */
public class TimelineChronologyAdapter<T extends ChronologicalInstant, S extends ChronologicalInstant> implements Chronology<T> {
	private ChronologyDomain<T> targetDomain;
    private ChronologyDomain<S> sourceDomain;
    private ChronologicalInterval<T> targetInterval;
    private ChronologicalInterval<S> sourceInterval;
    private Chronology<S>  sourceChronology;
    private List<ChronologicalEvent<T>> events;
	
    /**
     * Create a new chronology adapter. Points at the start of the source interval will be mapped to the 
     * start of the target interval ; points at the end of the source interval will be mapped to the end of 
     * the target interval ; other points will be mapped linearly along that scale.
     * @param targetDomain the domain in which outputs should be located
     * @param sourceChronology the chronology from which input events are retrieved
     * @param targetInterval the start and end points to which the source interval is mapped to produce an output
     * @param sourceInterval the start and end points which will be mapped to the target interval
     */
	public TimelineChronologyAdapter(ChronologyDomain<T> targetDomain,
			                         Chronology<S>       sourceChronology,
			                         ChronologicalInterval<T> targetInterval,
			                         ChronologicalInterval<S> sourceInterval) {
		this.targetDomain     = targetDomain;
		this.sourceChronology = sourceChronology;
		this.sourceDomain     = sourceChronology.getDomain();
		this.targetInterval   = targetInterval;
		this.sourceInterval   = sourceInterval;
		
		this.events = new ArrayList<ChronologicalEvent<T>>();
		for (ChronologicalEvent<S> event : sourceChronology.getEvents()) {
			events.add(new ConvertedEvent(event));
		}
	}
	
	@Override
	public ChronologyDomain<T> getDomain() {
		return targetDomain;
	}

	@Override
	public List<ChronologicalEvent<T>> getEvents() {
		return events;
	}

	@Override
	public List<ChronologicalEvent<T>> getEvents(
			ChronologicalInterval<T> interval) {
		// TODO: Trim some events
		return events;
	}
	
	@Override
	public boolean isMutable() {
		return false; // Adapted chronologies cannot support changes
	}

	@Override
	public boolean add(ChronologicalEvent<T> event, T start)
			throws IllegalArgumentException {
		return false; // Can't add to converted chronologies
	}


	@Override
	public boolean remove(ChronologicalEvent<T> event) {
		return false;
	}
	
	/**
	 * Map an instant of the input type to an instant of the output type, 
	 * according the parameters defined at construction.
	 * @param instant the instant to be mapped, which will be considered in terms of its linear relationship with the source interval
	 * @return a new instant of the output type which has the same linear relationship to the target interval
	 */
	public T convertInsant(S instant) {
		double v = sourceDomain.locateBetween(instant, sourceInterval.getStart(), sourceInterval.getEnd());
		return targetDomain.instantAt(targetInterval, v);
	}
	
	private class ConvertedEvent extends ChronologicalEvent<T> {

		private ChronologicalEvent<S> event;
		public ConvertedEvent (ChronologicalEvent<S> event) {
			super (convertInsant(event.getStart()), convertInsant(event.getEnd()));
			this.event = event;
		}
		
		@Override
		public Object getEventInfo() {
			return event.getEventInfo();
		}
		
		@Override
		public JComponent getRepresentation(Dimension preferredSize) {
			return event.getRepresentation(preferredSize);
		}
		
	}

}
