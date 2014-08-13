package gov.nasa.arc.mct.scenario.util;

public class Battery {
	private BatteryCapacityTable table = new BatteryCapacityTable();
	private static final int TOTAL_ENERGY = 5250; // total battery energy is 5250 Watt Hours
	public static final int TIME = 5; // calculate the battery state at every 5 minutes
	public static final int MITUTE_TO_HOUR = 60;
	public static final double TIME_TO_HOUR = TIME * 1.0 / MITUTE_TO_HOUR; 
	private double capacity = 100.0; // in percentage
	private static final int BATTERY_NUMBER = 8;
	
	public double setCapacity(double power, double duration) {
		if (duration == 0.0) duration = TIME_TO_HOUR;
		if (power != 0) {
			double change = power * duration / TOTAL_ENERGY;
			capacity -=  change * 100.0; 
		}
		return capacity;
	}
	
	public double getVoltage() {
		return table.getVoltage(this.capacity) * BATTERY_NUMBER;
	}
	
	public double getVoltage(double capacity) {
		return table.getVoltage(capacity) * BATTERY_NUMBER;
	}
	
	public double getCapacity() {
		return capacity;
	}
}
