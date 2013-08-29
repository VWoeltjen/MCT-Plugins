package gov.nasa.arc.mct.satellite.component;

import gov.nasa.arc.mct.api.feed.DataProvider;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.satellite.Trajectory;
import gov.nasa.arc.mct.satellite.Vector;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.satellite.utilities.SatTrak;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import name.gano.astro.time.Time;
import jsattrak.objects.SatelliteTleSGP4;

/*
 * used to be OrbitalDataProvider
 */
public class SatelliteDataProvider implements DataProvider {
	private static final double SPEEDUP          = 25.0;

	private static final Map<String, SatelliteModel>     models       = new HashMap<String, SatelliteModel>();
	private static final Map<String, List<Trajectory>> trajectories = new HashMap<String, List<Trajectory>>();   
	private static final Map<String, Long>             initialTime  = new HashMap<String, Long>();
	
	
	/* 
	 * (non-Javadoc)
	 * @see gov.nasa.arc.mct.api.feed.DataProvider#isFullyWithinTimeSpan(java.lang.String, long, java.util.concurrent.TimeUnit)
	 * 
	 *        the level of service expected from the Satellite data provider
	 *        this guy should be checking the feedID that it is an appropriate feed
	 */
	@Override
	public boolean isFullyWithinTimeSpan(String feedID, long startTime,
			TimeUnit timeUnit) {
		if( feedID.startsWith("SatOrbit:") && feedID.contains("/"))
			return true;
		else
			return false;
	}

	
	@Override
	public LOS getLOS() {
		return LOS.fast;
	}
	
/*
 * rather than thinking of trajectory data structure, thin k about having my satellite at this point
 * 
 * 
 * (non-Javadoc)
 * @see gov.nasa.arc.mct.api.feed.DataProvider#getData(java.util.Set, long, long, java.util.concurrent.TimeUnit)
 */
	@Override
	public Map<String, SortedMap<Long, Map<String, String>>> getData(
			Set<String> feedIDs, long startTime, long endTime, TimeUnit timeUnit) {
		Map<String, SortedMap<Long, Map<String, String>>> out = new HashMap<String, SortedMap<Long, Map<String, String>>>();
		
		for (String feedID : feedIDs) {
			if (feedID.startsWith("SatOrbit:") && feedID.contains("/")) {
				String tleFeed = feedID.substring(feedID.indexOf(":")+1);//+1 so we do not include ':'
				String[] pieces = tleFeed.split("/");
				//System.out.println( "[0]" + pieces[0]);  //satellite name
				//System.out.println( "[1]" + pieces[1]);  //TLE line 1
				//System.out.println( "[2]" + pieces[2]);  //TLE line 2
				//System.out.println( "[3]" + pieces[3]);  //position or velocity of x y or z component
				//boolean isVelocity = params.contains("v");
				SortedMap<Long, Map<String, String>> data = new TreeMap<Long, Map<String, String>>();
				for (long time = startTime; time < endTime; time += 1000) {
					double dataValue = 0.0;
					
					SatTrak sat = new SatTrak(pieces[0], pieces[1], pieces[2]);
					
					if (pieces[3].contains("x")) dataValue = sat.getECEFx();
					if (pieces[3].contains("y")) dataValue = sat.getECEFy();
					if (pieces[3].contains("z")) dataValue = sat.getECEFz();
					
					//System.out.println("Lat: " + sat.getLatitude()*180.0/Math.PI);
					//System.out.println("Lon: " + sat.getLongitude()*180.0/Math.PI);
					//System.out.println("data: " + dataValue);
					
					Map <String, String> dataItem = new HashMap<String, String> ();
					RenderingInfo ri = new RenderingInfo(Double.toString(dataValue), Color.ORANGE, " ", Color.ORANGE, true);
					dataItem.put(FeedProvider.NORMALIZED_RENDERING_INFO, ri.toString());
					dataItem.put(FeedProvider.NORMALIZED_TIME_KEY,  Long.toString(time));
					dataItem.put(FeedProvider.NORMALIZED_VALUE_KEY, Double.toString(dataValue));
					dataItem.put(FeedProvider.NORMALIZED_TELEMETRY_STATUS_CLASS_KEY, "1");
					data.put(Long.valueOf(time), dataItem);
				}
				if (!data.isEmpty()) {
					out.put(feedID, data);
				}
			}
		}
		return out;
	}

	@Override
	public Map<String, List<Map<String, String>>> getData(Set<String> feedIDs,
			TimeUnit timeUnit, long startTime, long endTime) {
		// TODO Auto-generated method stub
		return null;
	}
	
}


/*	Trajectory t = this.getTrajectory(name, time);
		//Vector  position = positions.get(index);
		Vector position = velocity ? t.getVelocity() : t.getPosition();
		//if (velocity) position = position.add(positions.get(index-1).multiply(-1.0));
		if (params.contains("x")) dataValue = position.getX();
		if (params.contains("y")) dataValue = position.getY();
		if (params.contains("z")) dataValue = position.getZ();
	*/
