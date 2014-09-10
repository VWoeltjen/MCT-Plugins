package gov.nasa.arc.mct.scenario.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class BatteryModel {
	private double batteryCapacity;
	private double initialStateOfCharge;
	
	public BatteryModel() {}
	
	public BatteryModel(double capacity, double stateOfCharge) {
		this.batteryCapacity = capacity;
		this.initialStateOfCharge = stateOfCharge;
	}
	
	public double getBatteryCapacity() {
		return batteryCapacity;
	}
	public void setBatteryCapacity(double capacity) {
		this.batteryCapacity = capacity;
	}
	public double getInitialStateOfCharge() {
		return initialStateOfCharge;
	}
	public void setInitialStateOfCharge(double initialStateOfCharge) {
		this.initialStateOfCharge = initialStateOfCharge;
	}
	
	public boolean isUninitialized() {
		return (batteryCapacity == 0.0) || (initialStateOfCharge == 0.0);
	}
}
