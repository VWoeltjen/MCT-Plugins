package gov.nasa.arc.mct.satellite.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class CoordinateModel {
	private static final String AXIS_KEY[] = {"x", "y", "z"};
	
	private boolean velocity = false;
	private int     axis = 0;
	private String  parent = "";
	
	public CoordinateModel() {
		
	}
	
	public CoordinateModel(int axis, boolean velocity, String parent) {
		this.axis     = axis;
		this.velocity = velocity;
		this.parent   = parent;
	}
	
	public String getParameterKey() {
		return (velocity ? "v" : "p") + AXIS_KEY[axis % AXIS_KEY.length];
	}
	
	public String getParent() {
		return parent;
	}
	
}
