package gov.nasa.arc.mct.satellite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class Vector {
	private double x, y, z;
	
	public Vector() {
		this (0,0,0);
	}
	
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public Vector add (Vector v) {
		return new Vector(getX() + v.getX(), getY() + v.getY(), getZ() + v.getZ());
	}
	
	public Vector multiply (double s) {
		return new Vector(getX() * s, getY() * s, getZ() * s);
	}
	
	public double dot (Vector v) {
		return getX() * v.getX() + getY() * v.getY() + getZ() * v.getZ();
	}
	
	public double magnitude() {
		return Math.sqrt(dot(this));
	}
	
	public Vector normal() {
		return multiply(1.0 / magnitude());
	}
	
	public Vector cross(Vector v) {
		return new Vector (getY() * v.getZ() - getZ() * v.getY(), 
				           getZ() * v.getX() - getX() * v.getZ(), 
				           getX() * v.getY() - getY() * v.getX()); 
	}
	
	@Override
	public boolean equals(Object v) {
		if (v instanceof Vector) {
			Vector vec = (Vector) v;
			return getX() == vec.getX() &&
			       getY() == vec.getY() &&
			       getZ() == vec.getZ();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Double.valueOf(getX()).hashCode() ^
		       Double.valueOf(getY()).hashCode() ^
		       Double.valueOf(getZ()).hashCode();
	}
}
