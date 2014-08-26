package gov.nasa.arc.mct.scenario.util;

import gov.nasa.arc.mct.scenario.component.BatteryModel;

/**
 * search for voltage value from the table for 
 * current battery state of charge.
 * 
 * @author jdong2
 *
 */
public class Battery {
	private BatteryVoltageTable table = new BatteryVoltageTable();	
	
	public static final int MITUTE_TO_HOUR = 60;
	private static final int BATTERY_NUMBER = 8;
	
	private double capacity = 5250.0; // total battery energy is 5250 Watt Hours
	private double initialStateOfCharge = 100.0;
	private double stateOfCharge = 100.0; // in percentage
		
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
		stateOfCharge = (stateOfCharge > 100.0)? 100.0 : stateOfCharge;
		return round(stateOfCharge);
	}
	
	private double getMinuteToHour(double duration) {
		return duration / MITUTE_TO_HOUR;
	}
	
	// modify stateOfCharge to be in 0.5% precision
	private double round(double stateOfCharge) {
		return BatteryVoltageTable.getNearestState(stateOfCharge);
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
