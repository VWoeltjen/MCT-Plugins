package gov.nasa.arc.mct.data.access;

import gov.nasa.arc.mct.api.feed.FeedDataArchive;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The <code>FeedDataArchiveAccess</code> class is used to inject an instance of the 
 * <code>FeedDataArchive</code> using declarative services. 
 * 
 * @author jdong2
 *
 */
public class FeedDataArchiveAccess {
	
	private static final AtomicReference<FeedDataArchive> feedDataArchive = 
			new AtomicReference<FeedDataArchive> ();
	
	public static FeedDataArchive getDataArchive() {
		return feedDataArchive.get();
	}
	
	public void setDataArchive(FeedDataArchive aDataArchive) {
		feedDataArchive.set(aDataArchive);
	}

	public void releaseDataArchive(FeedDataArchive aDataArchive) {
		feedDataArchive.set(null);
	}
}
