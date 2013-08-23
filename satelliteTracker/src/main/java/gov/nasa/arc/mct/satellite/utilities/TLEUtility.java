package gov.nasa.arc.mct.satellite.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsattrak.utilities.TLE;

import gov.nasa.arc.mct.satellite.utilities.TLEDownloader;

//import org.joda.time.DateTime;
//import org.joda.time.Days;

/*
 * A small class to handle accessing TLE files and collecting TLE data
 */
public class TLEUtility {	
	

	/*
	 * Note these files will be in the "Startup" project of MCT
	 */
	private final String localPathTLEData = "tmpDir/data/tle/";
	private final String pathToTLEAge = "tmpDir/data/tleAge.txt";
	//private final String localPathTLEUpdate    = "tmpDir/data/tle/";//"src/main/resources/data/tle/";
	
	private String absolutePath = ""; 
	
	private final int MAX_AGE_OF_TLES = 2; //in days
	
	/*
	 * Given a Celestrak TLE group-name (like 'Space Stations' for example) return the
	 * file where said TLEs are located
	 */
	@SuppressWarnings("serial")
	private static final Map<String, String> FileLookup = new HashMap<String,String>() {
		{
			//Special-Interest Satellites
			put("Last 30 Days' Launches", "tle-new.txt");
			put("Space Stations", "stations.txt");
			put("100 (or so) Brightest", "visual.txt");
			put("FENGYUN 1C Debris","1999-025.txt");
			put("IRIDIUM 33 Debris", "iridium-33-debris.txt");
			put("COSMOS 2251 Debris", "cosmos-2251-debris.txt");
			put("BREEZE-M R/B Breakup (2012-044C)", "2012-044.txt");
		
			//Weather & Earth Resources Satellites
			put( "Weather", "weather.txt");
			put("NOAA", "noaa.txt");
			put("GOES", "goes.txt");
			put("Earth Resources", "resource.txt");
			put("Search & Rescue (SARSAT)", "sarsat.txt");
			put("Disaster Monitoring", "dmc.txt");
			put("Tracking and Data Relay Satellite System (TDRSS)", "tdrss.txt");

			//Communications Satellites
			put("Geostationary", "geo.txt");
			put("Intelsat", "intelsat.txt");
			put("Gorizont", "gorizont.txt");
			put("Raduga", "raduga.txt");
			put("Molniya", "molniya.txt");
			put("Iridium", "iridium.txt");
			put("Orbcomm", "orbcomm.txt");
			put("Globalstar", "globalstar.txt");
			put("Amateur Radio", "amateur.txt");
			put("Experimental","x-comm.txt");
			put("Other", "other-comm.txt");
			
			//Navigation Satellites
			put("GPS Operational","gps-ops.txt");
			put("Glonass Operational","glo-ops.txt");
			put("Galileo","galileo.txt");
			put("Beidou","beidou.txt");
			put("Satellite-Based Augmentation System (WAAS/EGNOS/MSAS)","sbas.txt");
			put("Navy Navigation Satellite System (NNSS)","nnss.txt");
			put("Russian LEO Navigation","musson.txt");
			
			//Scientific Satellites
			put("Space & Earth Science","science.txt");
			put("Geodetic","geodetic.txt");
			put("Engineering","engineering.txt");
			put("Education","education.txt");

			//Miscellaneous Satellites
			put("Miscellaneous Military","military.txt");
			put("Radar Calibration","radar.txt");
			put("CubeSats","cubesat.txt");
			put("Other","other.txt");
		}
	};
	
	public
	TLEUtility() {
		absolutePath = new File(".").getAbsolutePath();
		absolutePath = absolutePath.substring(0, absolutePath.length()-1);
	}
	
	
	
	/*
	 * Given the full path to a file containing TLE data, e.g.: "stations.txt", this method returns
	 * a list of strings, each string is a line of the TLE file
	 */
	private  List<String>
	readFile(String filePath)
	{
		//System.out.println("File Location: " + filePath);
	    List<String> records = new ArrayList<String>();
	  try
	  {
		  BufferedReader reader = new BufferedReader(new FileReader(filePath));
		  //BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/tmpDir/data/tle/stations.txt")));
		  
	      String line;
	    while ((line = reader.readLine()) != null)
	    {
	      records.add(line);
	    }
	    reader.close();
	    return records;
	  }
	  catch (Exception e)
	  {
	    System.err.format("Exception occurred trying to read '%s'.", filePath);
	    e.printStackTrace();
	    return null;
	  }
	}//--end readFile
	
	/*
	 * This method determines the age of the TLEs we have on secondary storage
	 * If this method retuns false, an update to the TLEs is recomended (by calling
	 * method updateTLEs
	 */
	/*
	public	boolean
	haveRecentTLEs() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(pathToTLEAge));
			String str_dateOnFile = reader.readLine();
			reader.close();
			DateTime joda_dateOnFile = new DateTime(str_dateOnFile);
			Days days = Days.daysBetween(DateTime.now(), joda_dateOnFile);
			int daysOld = days.getDays();
			
			if(daysOld > MAX_AGE_OF_TLES )
				return false;
			else
				return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		//due to scoping issues with try/catch we add this return value; code will never reach here
		//either an exception will be thrown, or the if/else will return a value.
		return false;
	}*/
	
	/*
	 * This method updates all TLEs; fairly computationally heavy; first check if we need to do
	 * an update by calling method haveRecentTLEs.
	 * TODO: make a pretty progress bar :D
	 * returns true if all TLEs were downloaded successfully, otherwise returns false.
	 */
	public boolean
	updateTLEs() {
		
		TLEDownloader td = new TLEDownloader();
		td.setLocalPath(localPathTLEData);
		boolean wasSuccessfull = td.downloadAllTLEs();
		System.out.println("Path to TLEs: " + td.getTleFilePath(0));
		return wasSuccessfull;
		
	}
	
	
	/**
	 * Grab all the TLEs from a given TLE-file (from Celestrak) and return them as a List of TLEs
	 * 
	 * @param filename: file containing TLE data
	 * @return: list of TLE objects in the order given in the given TLE file
	 */
	private List<TLE>
	grab_tles(String fileLoc){
		
		List<String> raw_tle_data = readFile(fileLoc);
		/*
		 * all tle data are in three-line increments, so the size of raw_tle_data is 3 times the number
		 * of tle's in the file
		 * 
		 * Hence the number of tle's in the file is the number of elements in raw_tle_data divided by three
		 */
		int num_lines = raw_tle_data.size();
		List<TLE> tle_recs = new ArrayList<TLE>();
		//grab each tle:  lines 0 1 2 is the first tle, then lines 3 4 5 is the second tle, ... etc 
		for(int i = 0; i<num_lines; i =i+3) {
			TLE data = new TLE(raw_tle_data.get(i), raw_tle_data.get(i+1), raw_tle_data.get(i+2));
			tle_recs.add(data);
		}//end for loop
		
		return tle_recs; 	//list of tles
	}
	
	/*
	 * Given a , return the file where said TLE data is located; for example, if we want the "FENGYUN 1C Debris" TLE
	 * data, then we return 1999-025.txt
	 */
	private
	String
	getFileName(String RequestedTLEData) {
		String fileName = FileLookup.get(RequestedTLEData);
		if(fileName==null)
			return null;
		else
			return absolutePath + localPathTLEData + fileName;
	}
	
	
	
	/*
	 * Get all of the TLEs associated to the 'TLErequest'
	 *   e.g. request is for TLEs associated with "Last 30 Days' Launches" then
	 *        we return a list that contains all TLE's associated with "Last 30 Days' Launches"
	 * Preconditions: request must be a Celestrak satellite category
	 * Postconditions: returned list contains all TLE's associated with the TLE request
	 */
	public
	List<TLE>
	getTLEs (String TLErequest) {
		
		String tle_file = getFileName(TLErequest);
		
		List<TLE> tles =  grab_tles(tle_file);
		
		return tles;
	}
	
}
