package gov.nasa.arc.mct.scenario.util;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * parse the battery discharge table (in CSV format), and create new table 
 * with standard interval between different battery discharge states.
 * e.g. 77.5%, 78.0% V.S. 76.29%. 77.69%
 * read in the battery discharge capacity value, adjust the value to its nearest 50th
 * and look up the table for voltage value.
 * 
 * @author jdong2
 *
 */
public class BatteryVoltageTable {
	private static final String FILE = ResourceBundle.getBundle("Bundle").getString("BATTERY_FILE");
	
	// need to change to HashMap, using LinkedHashMap for debugging purposes
	private Map<Double, Double> voltageMap = new LinkedHashMap<Double, Double> ();
	private double modifiedState = 100.0; //the modified state of charge which are in 50th
	private boolean success = true;
	public static final double DISCHARGE_LIMIT = 30.0;
	private static final double INTERVAL = 0.5; // battery state of charge are in 0.5% precision
	
	public BatteryVoltageTable() {
		this.parseFile();
	}
	
	public void parseFile() {
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(FILE);
		if (in != null) {
			Scanner fileScanner = new Scanner(in, "UTF-8");
			while (fileScanner.hasNextLine() && success) {
				String line = fileScanner.nextLine();
				parseLine(line);
			}
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
	
	public Map getVoltageMap() {
		return voltageMap;
	}
	
	/**
	 * round the currentState to the nearest state based on the specified INTERVAL
	 * @param realState
	 * @return
	 */
	public static double getNearestState(double realState) {
		double upper = Math.ceil(realState / INTERVAL) * INTERVAL;
		double lower = Math.floor(realState / INTERVAL) * INTERVAL;
		double state = ((upper - realState) - (realState - lower) >= 0) ? lower : upper;
		return state;
	}
	
	public double getVoltage(double realState) {
		return voltageMap.get(getNearestState(realState));
	}
	
	
	public static boolean isBatteryFileExists() {
		return ClassLoader.getSystemClassLoader().getResourceAsStream(FILE) != null;
	}

	public static void main(String[] args) {			
		if (BatteryVoltageTable.isBatteryFileExists()) {
			BatteryVoltageTable table = new BatteryVoltageTable();
			for (Object key: table.getVoltageMap().keySet()) {
				System.out.println(key + ": " + table.getVoltageMap().get(key));
			}
			System.out.println(table.getVoltage(44.6) + ", " + table.getVoltage(85.36));
		}		
	}
}
