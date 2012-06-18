package gov.nasa.arc.mct.chronology.event;

import java.awt.Dimension;

import javax.swing.JComponent;


public abstract class ChronologicalEvent<T extends ChronologicalInstant> 
	extends ChronologicalInterval<T> {

	public ChronologicalEvent(T start, T end) {
		super(start, end);
	}
	
	public abstract Object getEventInfo();
	
	/**
	 * 
	 * @param preferredSize the preferred size for this representation (null if it is the event's choice)
	 * @return
	 */
	public abstract JComponent getRepresentation(Dimension preferredSize);
}
