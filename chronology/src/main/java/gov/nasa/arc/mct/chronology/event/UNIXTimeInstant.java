package gov.nasa.arc.mct.chronology.event;

import gov.nasa.arc.mct.chronology.ChronologyDomain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UNIXTimeInstant implements ChronologicalInstant {
	public static final String     DATE_FORMAT_STRING = "yyyy/DDD/HH:mm:ss";
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
	static { DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC")); }
	
	// Used to control the actual values of slices
	private static final int[] MARK_HIERARCHY = { 1, //   1 second 
		                                          5, // * 5 = 5 seconds
		                                          2, // * 2 = 10 seconds 
		                                          6, // * 6 = 1 minute 
		                                          5, // * 5 = 5 minutes
		                                          2, // * 2 = 10 minutes
		                                          6, // * 6 = 1 hour
		                                          3, // * 3 = 3 hours
		                                          2, // * 2 = 6 hours
		                                          4  // * 4 = 1 day
		                                          }; // Thereafter: Times ten!
	private static final int MINIMUM_INCREMENT = 1000;

	
	private long   timeMillis;
	
	public UNIXTimeInstant(long millis) {
		timeMillis = millis;
	}
	
	public long getTimeMillis() {
		return timeMillis;
	}
	
	public String toString() {
		return DATE_FORMAT.format(new Date(timeMillis));
	}
	
	@Override
	public int hashCode() {
		return Long.valueOf(timeMillis).intValue();
	}
	
	private static int getHierarchySize(int index) {
		if (index < MARK_HIERARCHY.length) return MARK_HIERARCHY[index];
		else return (index - MARK_HIERARCHY.length) % 2 == 0 ? 5 : 2;
	}
	
	
	private static final Comparator<UNIXTimeInstant> COMPARATOR =
		new Comparator<UNIXTimeInstant>() {
			@Override
			public int compare(UNIXTimeInstant o1, UNIXTimeInstant o2) {
				return Long.signum(o1.getTimeMillis() - o2.getTimeMillis());
			}
		};
	
	public static final ChronologyDomain<UNIXTimeInstant> DOMAIN =
		new ChronologyDomain<UNIXTimeInstant>() {
		
			@Override
			public UNIXTimeInstant convertToInstant(String instant)
					throws ParseException {
				return new UNIXTimeInstant(DATE_FORMAT.parse(instant).getTime());
			}

			@Override
			public Comparator<UNIXTimeInstant> getComparator() {
				return COMPARATOR;
			}

			@Override
			public double locateBetween(UNIXTimeInstant instant,
					UNIXTimeInstant a, UNIXTimeInstant b) {
				return (double) (instant.getTimeMillis() - a.getTimeMillis()) /
				       (double) (b.getTimeMillis()       - a.getTimeMillis());
			}

			@Override
			public List<UNIXTimeInstant> slice(
					ChronologicalInterval<UNIXTimeInstant> interval, int slices) {
				long increment = MINIMUM_INCREMENT;
				long intervalSize = interval.getEnd().getTimeMillis() - interval.getStart().getTimeMillis();
				intervalSize /= slices;
				int rank = 0;
				while (increment < intervalSize) {
					increment *= getHierarchySize(rank++);
				}
				
				
				List<UNIXTimeInstant> sliceList = new ArrayList<UNIXTimeInstant>();
				long sliceTime = (interval.getStart().getTimeMillis() - interval.getStart().getTimeMillis() % increment) + increment;
				while (sliceTime < interval.getEnd().getTimeMillis()) {
					sliceList.add(new UNIXTimeInstant(sliceTime));
					sliceTime += increment;
				}
				
				return sliceList;
			}

			@Override
			public UNIXTimeInstant instantAt(
					ChronologicalInterval<UNIXTimeInstant> interval,
					double proportion) {
				long start = interval.getStart().getTimeMillis();
				long end   = interval.getEnd().getTimeMillis();
				return new UNIXTimeInstant((long) (start + (end-start) * proportion));
			}
		};

	@Override
	public ChronologyDomain<? extends ChronologicalInstant> getDomain() {
		return DOMAIN;
	}
}
