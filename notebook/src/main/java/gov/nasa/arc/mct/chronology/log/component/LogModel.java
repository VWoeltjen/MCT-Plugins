package gov.nasa.arc.mct.chronology.log.component;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LogModel<T> {
	private List<LogEntry<T>> entries = new ArrayList<LogEntry<T>>();
	
	public void addEntry(T entry, List<AbstractComponent> components) {
		entries.add(new LogEntry<T>(entry, components));
	}
	
	public List<LogEntry<T>> getEntries() {
		return entries;
	}
}
