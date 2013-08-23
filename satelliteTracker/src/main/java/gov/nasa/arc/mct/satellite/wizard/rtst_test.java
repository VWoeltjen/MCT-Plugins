package gov.nasa.arc.mct.satellite.wizard;


/*
 * This code tests JSatTrak; this code tracks the ISS
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


import jsattrak.objects.SatelliteTleSGP4;
import jsattrak.utilities.TLE;
import jsattrak.utilities.TLEDownloader;

import java.lang.Math;

import name.gano.astro.time.Time;


public class rtst_test {
	
	/* WGS 84 Earth radius m
	 * WGS 84 first eccentricity squared
	 */
	protected static final double SEMI_MAJOR_AXIS = 6378137.0;
	protected static final double FIRST_ECCENTRICITY_SQUARED = 6.69437999014E-3;
	
	
	/*precondition:  lat is in radians*/
	private static final double N(double lat){
		return SEMI_MAJOR_AXIS/(1-FIRST_ECCENTRICITY_SQUARED*Math.pow(Math.sin(lat), 2));
	}
	
	/**
	 * Earth-Centered, Earth-Fixed x point
	 * precondition:  lat, lon are in radians
	 *                alt in meters
	 *                
	 *                Note: check out wikipedia for ECEF and ECI coordinates
	 */  
	public static
	double
	get_ECEF_x( double lat, double lon, double alt ) {
		return ((N(lat)+alt)*Math.cos(lat)*Math.cos(lon)/1000);
	}
	
	/**
	 * Earth-Centered, Earth-Fixed y point
	 * precondition:  lat, lon are in radians
	 *                alt in meters
	 */  
	public static
	double
	get_ECEF_y( double lat, double lon, double alt ) {
		return ((N(lat)+alt)*Math.cos(lat)*Math.sin(lon)/1000);
	}
	
	/**
	 * Earth-Centered, Earth-Fixed z point
	 * precondition:  lat, lon are in radians
	 *                alt in meters
	 *                
	 */  
	public static
	double
	get_ECEF_z( double lat, double lon, double alt ) {
		return ((N(lat)*(1-FIRST_ECCENTRICITY_SQUARED)+alt)*Math.sin(lat)/1000);
	}
	
	private static
	void
	wait_one_sec() {
		try {
		    Thread.sleep(1000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}//end wait_one_sec
	
	
	
	/*
	 * returns a list of strings, each string is a line of the file
	 */
	private static List<String> readFile(String filename)
	{
	  List<String> records = new ArrayList<String>();
	  try
	  {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
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
	    System.err.format("Exception occurred trying to read '%s'.", filename + "\n");
	    e.printStackTrace();
	    return null;
	  }
	}
	
	/**
	 * Grab and store all of the TLE's from a given file
	 * 
	 * @param filename: file containing TLE data
	 * @return: list of TLE objects in the order given in the given TLE file
	 */
	public static List<TLE>
	grab_tles(String filename){
		
		List<String> raw_tle_data = readFile(filename);
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
	
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		////this needs only be run once in a while
		TLEDownloader td = new TLEDownloader();
        @SuppressWarnings("unused")
		boolean result = td.downloadAllTLEs();
        /*
         * Postcondition: directory 'data' is created with all TLE text-files
         */
        
		
		//for this example, we are just going to track the ISS
        List<TLE> tle_data = grab_tles("data/tle/stations.txt");
        //Testing just the ISS
        //    Create SGP4 satellite propagator feeding into the constructor the name and the two lines of tle data
        SatelliteTleSGP4 prop = null;
        try
        {
            prop = new SatelliteTleSGP4(tle_data.get(0).getSatName(), tle_data.get(0).getLine1(), tle_data.get(0).getLine2());
            //prop.setShowGroundTrack(false); // if we arn't using the JSatTrak plots midas well turn this off to save CPU time
        }
        catch(Exception e)
        {
            System.out.println("Error Creating SGP4 Satellite");
            System.exit(1);
        }        
        Time currentJulianDate = new Time();
       
        for( int i=0; i< 1000; i=i+1) {
 
            currentJulianDate.update2CurrentTime();
            double jul_time = currentJulianDate.getJulianDate();
            System.out.println("Julian time from k  : " + jul_time);
            
            // prop to the desired time
            prop.propogate2JulDate(jul_time);
            System.out.println(prop.getTleAgeDays());
            
            // get the lat/long/altitude [radians, radians, meters]
            //double[] lla = prop.getLLA();
            
            System.out.println( "Name: " + tle_data.get(0).getSatName() + 
            					"\nLatitude[Deg]: "    + prop.getLatitude()*180.0/Math.PI +
            		            "\nLongitude[Deg]: " + prop.getLongitude()*180.0/Math.PI +
            		            "\nAltitude [m]: "   + prop.getAltitude() +
            		            "\nECEF x[m]:"       + get_ECEF_x(prop.getLatitude(), prop.getLongitude(), prop.getAltitude()) +
            		            "\nECEF y[m]:"       + get_ECEF_y(prop.getLatitude(), prop.getLongitude(), prop.getAltitude()) +
            		            "\nECEF z[m]:"       + get_ECEF_z(prop.getLatitude(), prop.getLongitude(), prop.getAltitude()) +
            		            "\n");
            
            
           // prop.ge
            wait_one_sec();
        }//end for loop
 
        
        System.out.println("Yo!");
        
	}


}
