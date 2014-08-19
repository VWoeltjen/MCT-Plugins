package gov.nasa.arc.mct.scenario.util;

import gov.nasa.arc.mct.scenario.component.BatteryModel;

/**
 * 
 * @author jdong2
 *
 */
public class Battery {
	private BatteryVoltageTable table = new BatteryVoltageTable();	
	
	public static final int TIME_INTERVAL = 5; // calculate the battery state at every 5 minutes
	public static final int MITUTE_TO_HOUR = 60;
	public static final double TIME_TO_HOUR = TIME_INTERVAL * 1.0 / MITUTE_TO_HOUR; 
	
	private double capacity = 5250.0; // total battery energy is 5250 Watt Hours
	private double initialStateOfCharge = 100.0;
	private double stateOfCharge = 100.0; // in percentage
	
	private static final int BATTERY_NUMBER = 8;
	
	public Battery(BatteryModel model) {
		this.capacity = model.getBatteryCapacity();
		this.initialStateOfCharge = model.getInitialStateOfCharge();
		this.stateOfCharge = initialStateOfCharge;
	}
	
	public double setStateOfCharge(double power, double duration) {
		duration = getMinuteToHour(duration);
		if (power != 0) {
			double change = power * duration / capacity;
			stateOfCharge -=  change * 100.0; 
		}
		return stateOfCharge;
	}
	
	private double getMinuteToHour(double duration) {
		return duration / MITUTE_TO_HOUR;
	}
	
	public double getVoltage() {
		return table.getVoltage(this.stateOfCharge) * BATTERY_NUMBER;
	}
	
	public double getVoltage(double stateOfCharge) {
		return table.getVoltage(stateOfCharge) * BATTERY_NUMBER;
	}
	
	public double getStateOfCharge() {
		return stateOfCharge;
	}
	
	public double getInitialStateOfCharge() {
		return initialStateOfCharge;
	}
}
