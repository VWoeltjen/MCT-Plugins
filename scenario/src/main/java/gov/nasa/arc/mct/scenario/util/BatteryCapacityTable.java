package gov.nasa.arc.mct.scenario.util;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * parse the battery discharge table, and create new table with standard interval 
 * between different battery discharge states.
 * e.g. 77.5%, 78.0% V.S.%76.29. %77.69
 * read in the battery discharge capacity value, adjust the value to its nearest 50th
 * and look up the table for voltage value.
 * 
 * @author jdong2
 *
 */
public class BatteryCapacityTable {
	private String fileName;
	// need to change to HashMap, using LinkedHashMap for debugging purposes
	private Map<Double, Double> capacities = new LinkedHashMap<Double, Double> ();
	private double fakeCapacity = 0.0;
	private boolean success = true;
	public static final double DISCHARGE_LIMIT = 70.0;
	private static final double INTERVAL = 0.5;
	
	public BatteryCapacityTable() {
		this("/Users/jdong2/Documents/NASA/RP Battery Model"
				+ "/Boston Power Swing 5300 SoC to Voltage Table.csv");
	}
	
	public BatteryCapacityTable(String file) {
		this.fileName = file;
	}
	
	public void parseFile() {
		File file = new File(fileName);
		try {
			Scanner fileScanner = new Scanner(file);
			while (fileScanner.hasNextLine() && success) {
				String line = fileScanner.nextLine();
				parseLine(line);
			}
		} catch (FileNotFoundException e) {
			System.out.println(fileName + "does not exist.");
			e.printStackTrace();
		}
	}
	
	private void parseLine(String line) {
		String[] values = line.split("%,");
		double realCapacity = Double.valueOf(values[0]);
		double voltage = Double.valueOf(values[2]);
		if (realCapacity <= DISCHARGE_LIMIT) {
			if (realCapacity != fakeCapacity) {
				while (fakeCapacity < realCapacity) {
					fakeCapacity += INTERVAL;
					capacities.put(fakeCapacity, voltage);					
				}				
			} else {
				capacities.put(fakeCapacity, voltage);
			}			
		} else {
			success = false;
		}				
	}
	
	public Map getCapacity() {
		return capacities;
	}
	
	private double getNearestCapacity(double realDischargeCapacity) {
		double upper = Math.ceil(realDischargeCapacity / INTERVAL) * INTERVAL;
		double lower = Math.floor(realDischargeCapacity / INTERVAL) * INTERVAL;
		double capacity = ((upper - realDischargeCapacity) - (realDischargeCapacity - lower) >= 0) ? lower : upper;
		return capacity;
	}
	
	public double getVoltage(double realDischargeCapacity) {
		return capacities.get(getNearestCapacity(realDischargeCapacity));
	}

	public static void main(String[] args) {
		BatteryCapacityTable table = new BatteryCapacityTable();
		table.parseFile();
		for (Object key: table.getCapacity().keySet()) {
			System.out.println(key + ": " + table.getCapacity().get(key));
		}
		System.out.println(table.getVoltage(44.6) + ", " + table.getVoltage(5.36));
	}
}
