package gov.nasa.arc.mct.nontimeplot.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.gui.FeedView;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NonTimePlotView extends FeedView {
	private static final long serialVersionUID = -8332691253144683655L;

	private static final String SEPARATOR = "////";
	
	private List<FeedProvider> feedProviders = new ArrayList<FeedProvider>();

	private NonTimePlot plot = new NonTimePlot();
	
	public NonTimePlotView (AbstractComponent ac, ViewInfo vi) {
		super (ac, vi);
		
		for (AbstractComponent child : ac.getComponents()) {
			FeedProvider fp = child.getCapability(FeedProvider.class);
			if (fp != null) {
				feedProviders.add(fp);
			}
		}
		
		if (feedProviders.size() >= 2) {
			plot.addDataset(key(), Color.PINK);
		}
		
		add (plot);
		
	}
	
	@Override
	public void updateFromFeed(Map<String, List<Map<String, String>>> data) {
		if (feedProviders.size() < 2) return;
		Double x = getData(feedProviders.get(0), data);
		Double y = getData(feedProviders.get(1), data);
		if (x != null && y != null) {
			plot.addPoint(key(), x, y);
		}		
	}

	@Override
	public void synchronizeTime(Map<String, List<Map<String, String>>> data,
			long syncTime) {
		updateFromFeed(data);		
	}

	@Override
	public Collection<FeedProvider> getVisibleFeedProviders() {
		return feedProviders;
	}
	
	private Double getData(FeedProvider fp, Map<String, List<Map<String,String>>> data) {
		String id = fp.getSubscriptionId();
		if (data.containsKey(id)) {
			List<Map<String, String>> series = data.get(id);
			if (series.size() > 0) {
				Map<String, String> point = series.get(series.size() - 1);
				RenderingInfo ri = fp.getRenderingInfo(point);
				if (ri != null) {
					String value = ri.getValueText();					
					try {
						return Double.parseDouble(value);
					} catch (Exception e) {
						return null;
					}
				}
			}
		}
		return null;
	}
	
	private String key () {
		return (feedProviders.size() >= 2) ?
				(feedProviders.get(0).getSubscriptionId() +
				SEPARATOR +
				feedProviders.get(1).getSubscriptionId()) :
				"";
	}
	
	private Long getTimestamp(Map<String, String> dataPoint) {
		if (dataPoint.containsKey(FeedProvider.NORMALIZED_TIME_KEY)) {
			String timestamp = dataPoint.get(FeedProvider.NORMALIZED_TIME_KEY);
			try {
				return Long.parseLong(timestamp);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	

}
