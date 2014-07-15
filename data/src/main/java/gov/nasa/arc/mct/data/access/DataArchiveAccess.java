package gov.nasa.arc.mct.data.access;

import gov.nasa.arc.mct.api.feed.FeedDataArchive;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The <code>DataArchiveAccess</code> class is used to inject an instance of the 
 * <code>DataArchive</code> using declarative services. 
 * 
 * @author jdong2
 *
 */
public class DataArchiveAccess {
	
	private static final AtomicReference<FeedDataArchive> dataArchive = 
			new AtomicReference<FeedDataArchive> ();
	
	public static FeedDataArchive getDataArchive() {
		return dataArchive.get();
	}
	
	public void setDataArchive(FeedDataArchive aDataArchive) {
		dataArchive.set(aDataArchive);
	}

	public void releaseDataArchive(FeedDataArchive aDataArchive) {
		dataArchive.set(null);
	}
}
