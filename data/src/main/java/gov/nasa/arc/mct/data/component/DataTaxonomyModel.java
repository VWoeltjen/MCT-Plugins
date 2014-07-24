package gov.nasa.arc.mct.data.component;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/** annotation for using JAXB
 *  stating 
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)

/**
 * Model to store the end time stamp for DataComponent. 
 * This is needed  so that getTimeService() in DataComponent 
 * returns current time appropriate to its data Source.
 * 
 * @author jdong2
 *
 */
public class DataTaxonomyModel {
	private Map<String, Long> endTimeMap = new HashMap<String, Long> ();

	public Map<String, Long> getTimeMap() {
		return endTimeMap;
	}
	
	public DataTaxonomyModel setTime(String id, String time) {
		endTimeMap.put(id, Long.parseLong(time));
		return this;
	}
	
	public Boolean contains(String id) {
		return endTimeMap.containsKey(id);
	}
	
	public long getEndTime(String id) {
		return endTimeMap.get(id);
	}
}
