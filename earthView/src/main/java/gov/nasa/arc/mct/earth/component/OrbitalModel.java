package gov.nasa.arc.mct.earth.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import gov.nasa.arc.mct.earth.Trajectory;
import gov.nasa.arc.mct.earth.Vector;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class OrbitalModel {
	private Vector initialPosition = new Vector(0.0, 0.0, 0.0);
	private Vector initialVelocity = new Vector(0.0, 0.0, 0.0);
	private long   initialTime     = System.currentTimeMillis();
	
	public void set(Vector pos, Vector vel, long start) {
		initialPosition = pos;
		initialVelocity = vel;
		initialTime     = start;
	}
	
	public Trajectory getInitialTrajectory() {
		return new Trajectory() {

			@Override
			public Vector getPosition() {
				return initialPosition;
			}

			@Override
			public Vector getVelocity() {
				return initialVelocity;
			}
			
		};
	}
	
	public long getInitialTime() {
		return initialTime;
	}
	
}
