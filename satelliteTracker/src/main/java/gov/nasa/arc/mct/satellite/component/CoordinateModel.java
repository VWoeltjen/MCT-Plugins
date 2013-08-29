package gov.nasa.arc.mct.satellite.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import jsattrak.utilities.TLE;


@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class CoordinateModel {
	
	private static final String AXIS_KEY[] = {"x", "y", "z"};
	
	private boolean velocity = false;
	private int     axis = 0;
	private String  parent = "";
	
	/*
	 * Note: because of JABX, we must store the TLE data as individual strings and not as a TLE object
	 */
	private String tleSatName;
	private String tleLine1;
	private String tleLine2;
	
	public CoordinateModel() {
		
	}
	
	public CoordinateModel(int axis, boolean velocity, String parent) {
		this.axis     = axis;
		this.velocity = velocity;
		this.parent   = parent;
	}
	
	public CoordinateModel(int axis, boolean velocity, String parent, TLE current) {
		this.axis     = axis;
		this.velocity = velocity;
		this.parent   = parent;
		//this.associatedTLE = current;
		this.tleSatName=current.getSatName();
		this.tleLine1=current.getLine1();
		this.tleLine2=current.getLine2();
	}
	
	public String getParameterKey() {
		return (velocity ? "v" : "p") + AXIS_KEY[axis % AXIS_KEY.length];
	}
	
	public String getParent() {
		return parent;
	}
	
	public TLE getTLE(){
		return (new TLE(this.tleSatName, this.tleLine1, this.tleLine2));
	}
	
}
