package gov.nasa.arc.mct.scenario.util;

public class Battery {
	private BatteryCapacityTable table = new BatteryCapacityTable();
	private static final int TOTAL_ENERGY = 5250; // total battery energy is 5250 Watt Hours
	public static final double TIME_RANGE = 5.0 / 60.0; // calculate the battery state at every 5 minutes
	private double dischargeCapacity = 0.0;
	
	public double setDischargeCapacity(double power) {
		if (power != 0) {
			dischargeCapacity -= power * TIME_RANGE / TOTAL_ENERGY; 
		}
		return dischargeCapacity;
	}
	
	public double getVoltage() {
		return table.getVoltage(dischargeCapacity);
	}
}
