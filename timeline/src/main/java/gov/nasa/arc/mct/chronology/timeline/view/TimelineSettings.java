package gov.nasa.arc.mct.chronology.timeline.view;

import gov.nasa.arc.mct.components.ExtendedProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Described the settings for a timeline view; serves as an intermediary between view properties 
 * and a TimelineView.
 * @author vwoeltje
 *
 */
public class TimelineSettings {
	private static final String START_MAP_KEY = "TimelineStartMap";
	private static final String END_MAP_KEY   = "TimelineEndMap";
	private static final String START_KEY     = "TimelineStart";
	private static final String END_KEY       = "TimelineEnd";
	
	
	private Map<String, String> startMap = new HashMap<String, String>();
	private Map<String, String> endMap   = new HashMap<String, String>();
	private String start = "";
	private String end   = ""; 
	
	private TimelineView managedView;

	/** 
	 * Create a new object to handle the settings for a view.
	 * @param managedView the Timeline View whose settings will be managed
	 */
	public TimelineSettings(TimelineView managedView) {
		this.managedView = managedView;
		load();
	}
	
	/**
	 * Pull settings from view properties
	 */
	public void load() {
		ExtendedProperties props = managedView.getViewProperties();
		
		String prop = props.getProperty(START_MAP_KEY, String.class);
		if (prop != null) startMap = deserialize(prop);
		
		prop = props.getProperty(END_MAP_KEY, String.class);
		if (prop != null) endMap = deserialize(prop);
		
		prop = props.getProperty(START_KEY, String.class);
		if (prop != null) start = prop;
		
		prop = props.getProperty(END_KEY, String.class);
		if (prop != null) end = prop;
	}
	
	/**
	 * Save a timeline view's properties to persistence
	 */
	public void save() {
		ExtendedProperties props = managedView.getViewProperties();
		
		props.setProperty(START_MAP_KEY, serialize(startMap));
		props.setProperty(END_MAP_KEY,   serialize(endMap)  );
		props.setProperty(START_KEY,     start              );
		props.setProperty(END_KEY,       end                );
		
	}
	
	/**
	 * Set the minimum / maximum displayable region for the managed Timeline (note that these 
	 * are raw String values; it is the responsibility of the caller to ensure that these can 
	 * be parsed by the chronology domain appropriate to the timeline.)
	 * @param start the minimum displayable instant
	 * @param end   the maximum displayable instant
	 */
	public void setBounds(String start, String end) {
		this.start = start;
		this.end   = end;
	}
	
	/**
	 * Add a new adapter, used to display time lines of differing domains. Note that start and end times 
	 * are raw String values; it is the responsibility of the caller to ensure that these can be 
	 * parsed by the chronology domain appropriate to the timeline. 
	 * @param name the component ID with which to associate this adapter
	 * @param start the point in the destination domain which corresponds to the start of the source chronology
	 * @param end the point in the destination domain which corresponds to the end of the source chronology
	 */
	public void addAdapter(String name, String start, String end) {
		startMap.put(name, start);
		endMap.put(name, end);
	}
	
	/**
	 * Get the minimum displayable point in time. Note this is represented as a String; the caller is 
	 * responsible for converting it to the appropriate time domain.
	 * @return the minimum displayable instant for this view
	 */
	public String getStart() {
		return start;
	}

	/**
	 * Get the maximum displayable point in time. Note this is represented as a String; the caller is 
	 * responsible for converting it to the appropriate time domain.
	 * @return the maximum displayable instant for this view
	 */
	public String getEnd() {
		return end;
	}
	
	/**
	 * Get an instant which corresponds to the start of a chronology provided by the identified component. 
	 * This is used to create adapters for chronologies along different domains. The return value 
	 * corresponds to the point in the displayed domain to which the other chronology's start should be mapped.
	 * Note that this is represented as a String; the caller is responsible for converting it to the 
	 * appropriate time domain.
	 * @return the point at which to start the adapted chronology
	 */
	public String getStart(String name) {
		return startMap.get(name);
	}

	/**
	 * Get an instant which corresponds to the end of a chronology provided by the identified component. 
	 * This is used to create adapters for chronologies along different domains. The return value 
	 * corresponds to the point in the displayed domain to which the other chronology's end should be mapped.
	 * Note that this is represented as a String; the caller is responsible for converting it to the 
	 * appropriate time domain.
	 * @return the point at which to end the adapted chronology
	 */
	public String getEnd(String name) {
		return endMap.get(name);
	}
	
	private Map<String, String> deserialize(String str) {
		Map<String, String> result = new HashMap<String, String>();
		StringTokenizer entries = new StringTokenizer (str, "\n");
		while (entries.hasMoreTokens()) {
			StringTokenizer pairs = new StringTokenizer(str, "\t");
			try {
				String key   = pairs.nextToken();
				String value = pairs.nextToken();
				result.put(key, value);
			} catch (NoSuchElementException e) {
				//TODO: Log broken property 
			}
		}
		return result;
	}
	
	private String serialize(Map<String, String> map) {
		String result = "";
		for (Entry<String, String> entry : map.entrySet()) {
			result = entry.getKey() + "\t" + entry.getValue() + "\n";
		}
		return result;
	}
	
}
