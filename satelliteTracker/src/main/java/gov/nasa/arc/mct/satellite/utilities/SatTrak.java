package gov.nasa.arc.mct.satellite.utilities;

import jsattrak.objects.SatelliteTleSGP4;
import name.gano.astro.time.Time;


public class SatTrak {
	/*Units returned from the satellite object:
	 *    getLatitude is in radians
	 *    getLongitude is in radians
	 *    getAltitude is in meters
	 */
	private SatelliteTleSGP4 prop;
	private Time julianTime;

	private final double SEMI_MAJOR_AXIS = 6378137.0;
	private final double FIRST_ECCENTRICITY_SQUARED = 6.69437999014E-3;
	
	public
	SatTrak	(String satName, String tleLine1, String tleLine2) {
		julianTime = new Time();
		try {
			prop = new SatelliteTleSGP4(satName, tleLine1, tleLine2);
		} catch (Exception e) {
			System.out.println("Error Creating SGP4 Satellite");
			e.printStackTrace();
		}
		prop.setShowGroundTrack(false); // if we arn't using the JSatTrak plots midas well turn this off to save CPU time
	}
	public void
	updateSat2CurTime() {
		julianTime.update2CurrentTime();
		prop.propogate2JulDate(julianTime.getJulianDate());
	}
	
	/*precondition:  lat is in radians*/
	private double
	N(double lat){
		return SEMI_MAJOR_AXIS/(1-FIRST_ECCENTRICITY_SQUARED*Math.pow(Math.sin(lat), 2));
	}
	
	/**
	 * This method updates the satelite object to the current system time (in milliseconds) and 
	 * returns the ECEF x-coordinate
	 * 
	 * Use this method if you do not want to be bothered by updating your satellite to the current
	 * time
	 * 
	 * @return: Earth Centered Earth Fixed x-coordinate at the current system time
	 */
	public double getECEFx() {
		updateSat2CurTime();
		return ((N(prop.getLatitude())+prop.getAltitude())*Math.cos(prop.getLatitude())*Math.cos(prop.getLongitude())/1000);
	}
	
	public double getECEFy() {
		updateSat2CurTime();
		return ((N(prop.getLatitude())+prop.getAltitude())*Math.cos(prop.getLatitude())*Math.sin(prop.getLongitude())/1000);
	}
	
	public double getECEFz() {
		updateSat2CurTime();
		return ((N(prop.getLatitude())*(1-FIRST_ECCENTRICITY_SQUARED)+prop.getAltitude())*Math.sin(prop.getLatitude())/1000);
	}
	
	public double getLatitude() {
		updateSat2CurTime();
		return prop.getLatitude();
	}
	public double getLongitude() {
		updateSat2CurTime();
		return prop.getLongitude();
	}
	public double getAltitude() {
		updateSat2CurTime();
		return prop.getAltitude();
	}
	
	public String getSatName() {
		return prop.getName();
	}
	
	
	
	/*
	 * Here is a little main to test the features of SatTrak and to verify that it is getting correct
	 * data.  the ISS TLE file is located at http://celestrak.com/NORAD/elements/stations.txt
	 * be sure to have the most recent TLE and then pop onto http://www.isstracker.com/ to see if the
	 * below code is tracking the ISS properly
	 */
	public static void main(String[] args) {
		String name = "ISS (ZARYA)             ";
		String tleLine1 = "1 25544U 98067A   13241.39990741  .00008176  00000-0  14800-3 0  4862";
		String tleLine2 = "2 25544  51.6499 105.5504 0004476  12.3451 332.2743 15.50589082846053";
		
		SatTrak sat = new SatTrak(name, tleLine1, tleLine2);
		
		for( int i=0; i< 1000; i=i+1) {
			 //sat.updateSat2CurTime();
			
			 System.out.println( "Name: " + sat.getSatName() + 
 					"\nLatitude[Deg]: "    + sat.getLatitude()*180.0/Math.PI +
 		            "\nLongitude[Deg]: " + sat.getLongitude()*180.0/Math.PI +
 		            "\nAltitude [m]: "   + sat.getAltitude() +
 		            "\nECEF x[m]:"       + sat.getECEFx() +
 		            "\nECEF y[m]:"       + sat.getECEFy() +
 		            "\nECEF z[m]:"       + sat.getECEFz() +
 		            "\n");
			 
			 
			 try {
			 	    Thread.sleep(1000);
			 } catch(InterruptedException ex) {
			 	    Thread.currentThread().interrupt();
			 }
		}
	}
}
