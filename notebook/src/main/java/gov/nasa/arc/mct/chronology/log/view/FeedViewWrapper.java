package gov.nasa.arc.mct.chronology.log.view;

import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.gui.FeedView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.event.AncestorListener;

public class FeedViewWrapper extends FeedView {
	private static final long serialVersionUID = 2455053417017499033L;

	private FeedView feedView;
	
	public FeedViewWrapper(FeedView view, final long time) {
		super(view.getManifestedComponent(), view.getInfo());
		
		feedView = view;
		for (AncestorListener al : feedView.getAncestorListeners())
			feedView.removeAncestorListener(al);
		
		add(feedView);
		
		super.requestData(feedView.getVisibleFeedProviders(), time - 1000*60*30, time, null, 
				new RenderingCallback() {

					@Override
					public void render(
							Map<String, List<Map<String, String>>> data) {
						feedView.updateFromFeed(data);
						feedView.synchronizeTime(data, time);
					}
			
				}, false);
	}
	
	@Override
	public void updateFromFeed(Map<String, List<Map<String, String>>> data) {
		return; // We don't care about new, real data!
	}

	@Override
	public void synchronizeTime(Map<String, List<Map<String, String>>> data,
			long syncTime) {
		return; // We don't care about new, real data!
	}

	@Override
	public Collection<FeedProvider> getVisibleFeedProviders() {
		return feedView.getVisibleFeedProviders();
	}




}
