package gov.nasa.arc.mct.chronology.event;


public class ChronologicalInterval<T extends ChronologicalInstant> {
	/**
	 * Start time, in milliseconds since UNIX epoch (January 1, 1970)
	 */
	private T start;
	
	/**
	 * End time, in milliseconds since UNIX epoch (January 1, 1970)
	 */
	private T end;

	/**
	 * Specify a chronological interval for the given start and end times.
	 * @param start the beginning of the interval
	 * @param end   the termination of the interval
	 */
	public ChronologicalInterval(T start, T end) {
		this.start = start;
		this.end   = end;
	}
	
	
	public T getStart() {
		return start;
	}
	
	public T getEnd()   {
		return end;
	}
	
}
