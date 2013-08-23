/**
 * 
 */
package test;

import gov.nasa.arc.mct.satellite.utilities.TLEDownloader;
import gov.nasa.arc.mct.satellite.utilities.TLEUtility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jsattrak.objects.SatelliteTleSGP4;
import jsattrak.utilities.TLE;
import name.gano.astro.time.Time;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * @author hcmarsh
 *
 */
public class Sandbox {

	private static final Map<String, String> FileLookup = new HashMap<String,String>() {
		{
			//Special-Interest Satellites
			put("Last 30 Days' Launches", "tle-new.txt");
			put("Space Stations", "stations.txt ");
			put("100 (or so) Brightest", "visual.txt ");
			put("FENGYUN 1C Debris","1999-025.txt");
			put("IRIDIUM 33 Debris", "iridium-33-debris.txt");
			put("COSMOS 2251 Debris", "cosmos-2251-debris.txt");
			put("BREEZE-M R/B Breakup (2012-044C)", "2012-044.txt");
		}
	};
		
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
/*
		String result  = FileLookup.get("BREEZE-M R/B Breakup (2012-044C)");
		
		if(result!=null)
			System.out.println(result);
		else
			System.out.println("Nothing Found");
		
		TLEUtility tleUtil = new TLEUtility();
		//boolean got = tleUtil.td.downloadAllTLEs();
		// System.out.println("Update of TLEs was sucessful? : " + got);
		
		DateTime dt = new DateTime();
		Days d = Days.daysBetween(DateTime.now(), DateTime.now());
		int numDays = d.getDays();

		System.out.println(DateTime.now().toString());
		
		String content = DateTime.now().toString();
*/		
		/*		
		try {			
 
			File file = new File("/Users/hcmarsh/Documents/Development/MyGitHubRepository/satellite_plugin_mct/MCT-Plugins/satelliteTracker/src/main/resources/tleAge.txt");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}*/		
/*		
		try {
			FileReader fr = new FileReader("src/main/resources/tleAge.txt");
			BufferedReader reader = new BufferedReader(fr);
			String str_dateOnFile = reader.readLine();
			System.out.println("File Contents (string): " + str_dateOnFile );
			DateTime joda_dateOnFile = new DateTime(str_dateOnFile);
			System.out.println("File Contents (string): " + joda_dateOnFile.now().toString() );
			Days days = Days.daysBetween(DateTime.now(), joda_dateOnFile);
			int TLEage = days.getDays();
			System.out.println("Age of TLE file: (string): " + TLEage );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TLEUtility tu = new TLEUtility();
*/		
		//List<TLE> choices = tu.getTLEs("Last 30 Days' Launches");
		/*
		if (tu.haveRecentTLEs())
			System.out.println("TLEs are recent");
		else
			System.out.println("TLEs are old");
		*/
/*		try {
			BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/data/tle/stations.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Lets Go!");
		List<String> fileContents = tu.readFile("file");
		
		System.out.println("End!");
		//System.out.println(tu.updateTLEs());
		*/
		//System.out.println("Do an Update");
		//tu.updateTLEs();
		//System.out.println("Finished Update");
		//TLEDownloader td = new TLEDownloader();
		//System.out.println("Got all TLEs?: " + td.downloadAllTLEs());
//		List<TLE> userChoices = tu.getTLEs("Space Stations");
//		System.out.println("end");		
	}
}

/*
 * old way to test the age of tle files: problem: too much file access
 * 
		List<TLE> tle_data = grab_tles( localPath + "stations.txt");
        SatelliteTleSGP4 prop = null;
        try
        {
            prop = new SatelliteTleSGP4(tle_data.get(0).getSatName(), tle_data.get(0).getLine1(), tle_data.get(0).getLine2());
            prop.setShowGroundTrack(false); // if we arn't using the JSatTrak plots midas well turn this off to save CPU time
        }
        catch(Exception e)
        {
            System.out.println("Error Creating SGP4 Satellite");
            System.exit(1);
        }
        Time currentJulianDate = new Time();
        currentJulianDate.update2CurrentTime();
        prop.propogate2JulDate(currentJulianDate.getJulianDate());
        
        if( prop.getTleAgeDays() > 2 )
        	td.downloadAllTLEs();
 * 
 * 
 * 
 */