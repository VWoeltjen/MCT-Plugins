package gov.nasa.arc.mct.chronology;

import gov.nasa.arc.mct.chronology.event.ChronologicalInstant;
import gov.nasa.arc.mct.chronology.event.ChronologicalInterval;

import java.text.ParseException;
import java.util.Comparator;
import java.util.List;

/**
 * A ChronologyDomain is responsible for managing the sequential relationships of objects of similar type. 
 * It encapsulates the majority of the logic for dealing with sequences, allowing them to be dealt with in a 
 * reasonably generic way.
 * 
 * @author vwoeltje
 *
 * @param <T> the type of object which would describe a point within this domain 
 */
public abstract class ChronologyDomain<T extends ChronologicalInstant> {
	/**
	 * Attempts to convert some String representation of an instant to the object form normally used 
	 * to its corresponding ChronologicalInstant form. 
	 * @param instant a string representation of an instant
	 * @return an object which describes a point within this domain
	 * @throws ParseException indicates that the provided String does not describe a recognizable object in this domain
	 */
	public abstract T             convertToInstant(String instant) throws ParseException;
	
	/**
	 * Get a comparator to compare instants within domain. This comparator is used to impose an order upon 
	 * a set of instants & of events.
	 * @return a comparator for instants within this domain
	 */
	public abstract Comparator<T> getComparator();
	
	/**
	 * Checks to determine if one interval is contained within another.
	 * @param a the first interval
	 * @param b the second interval
	 * @return true if all instants for the second are contained within the first
	 */
	public boolean contains(ChronologicalInterval<T> a, ChronologicalInterval<T> b) { // Does interval a contain b?
		Comparator<T> cmp = getComparator();
		return cmp.compare(b.getStart(), a.getStart()) >= 0 &&
		       cmp.compare(b.getEnd(),   a.getEnd())   <= 0;
	}
	
	/**
	 * Given two instants along this domain, determine where between them a third instant belongs. This 
	 * is evaluated as a floating point value which should describe a linear relationship between the two 
	 * points, where point a lies at 0.0, point b lies at 1.0, and points in between or beyond lie at 
	 * proportional values. 
	 * @param instant the instant to locate within the interval
	 * @param a the start of the interval
	 * @param b the end of the interval
	 * @return the proportional location of the instant between points a & b
	 */
	public abstract double locateBetween(T instant, T a, T b);
	
	/**
	 * 
	 * @param interval
	 * @param slices
	 * @return
	 */
	public abstract List<T> slice(ChronologicalInterval<T> interval, int slices);
	
	/**
	 * 
	 * @param interval
	 * @param proportion
	 * @return
	 */
	public abstract T       instantAt(ChronologicalInterval<T> interval, double proportion);
	
	/**
	 * 
	 * @param interval
	 * @param p1
	 * @param p2
	 * @return
	 */
	public ChronologicalInterval<T> getSubInterval(ChronologicalInterval<T> interval, double p1, double p2) {
		return new ChronologicalInterval<T> (instantAt(interval, p1), instantAt(interval, p2));
	}
}
