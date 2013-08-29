package gov.nasa.arc.mct.satellite.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import jsattrak.utilities.TLE;
import gov.nasa.arc.mct.satellite.Trajectory;
import gov.nasa.arc.mct.satellite.Vector;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SatelliteModel {
	private Vector initialPosition = new Vector(0.0, 0.0, 0.0);
	private Vector initialVelocity = new Vector(0.0, 0.0, 0.0);
	private long   initialTime     = System.currentTimeMillis();
	
	private TLE SatTLE;
	
	/*This method, 'set', should accept a TLE, then create the satellite based on that TLE
	 * 
	 */
	public void set(Vector pos, Vector vel, long start) {
		initialPosition = pos;
		initialVelocity = vel;
		initialTime     = start;
	}
	
	public void set( TLE tle ) {
		SatTLE = tle;	//new TLE( tle.getSatName(), tle.getLine1(), tle.getLine2());
	}

}
