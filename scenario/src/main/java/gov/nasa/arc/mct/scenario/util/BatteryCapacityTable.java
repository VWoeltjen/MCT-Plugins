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
 * e.g. 77.5%, 78.0% V.S. 76.29%. 77.69%
 * read in the battery discharge capacity value, adjust the value to its nearest 50th
 * and look up the table for voltage value.
 * 
 * @author jdong2
 *
 */
public class BatteryCapacityTable {
	private String fileName;
	// need to change to HashMap, using LinkedHashMap for debugging purposes
	private Map<Double, Double> voltageMap = new LinkedHashMap<Double, Double> ();
	private double modifiedState = 100.0; // modify the state of charge to be in 50th
	private boolean success = true;
	public static final double DISCHARGE_LIMIT = 30.0;
	private static final double INTERVAL = 0.5;
	
	public BatteryCapacityTable() {
		this("/Users/jdong2/Documents/NASA/RP Battery Model"
				+ "/Boston Power Swing 5300 SoC to Voltage Table.csv");
	}
	
	public BatteryCapacityTable(String file) {
		this.fileName = file;
		this.parseFile();
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
		double standardState = Double.valueOf(values[1]);
		double voltage = Double.valueOf(values[2]);
		if (standardState >= DISCHARGE_LIMIT) {
			if (standardState != modifiedState) {
				while (modifiedState > standardState) {
					modifiedState -= INTERVAL;
					voltageMap.put(modifiedState, voltage);					
				}				
			} else {
				voltageMap.put(modifiedState, voltage);
			}			
		} else {
			success = false;
		}				
	}
	
	public Map getCapacityMap() {
		return voltageMap;
	}
	
	private double getNearestCapacity(double realCapacity) {
		double upper = Math.ceil(realCapacity / INTERVAL) * INTERVAL;
		double lower = Math.floor(realCapacity / INTERVAL) * INTERVAL;
		double capacity = ((upper - realCapacity) - (realCapacity - lower) >= 0) ? lower : upper;
		return capacity;
	}
	
	public double getVoltage(double realCapacity) {
		return voltageMap.get(getNearestCapacity(realCapacity));
	}

	public static void main(String[] args) {
		BatteryCapacityTable table = new BatteryCapacityTable();
		table.parseFile();
		for (Object key: table.getCapacityMap().keySet()) {
			System.out.println(key + ": " + table.getCapacityMap().get(key));
		}
		// System.out.println(table.getVoltage(44.6) + ", " + table.getVoltage(85.36));
	}
}
