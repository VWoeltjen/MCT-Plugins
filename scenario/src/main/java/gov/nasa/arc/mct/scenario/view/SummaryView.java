package gov.nasa.arc.mct.scenario.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.scenario.component.CostFunctionCapability;
import gov.nasa.arc.mct.scenario.component.CostFunctionComponent;
import gov.nasa.arc.mct.scenario.component.DurationCapability;
import gov.nasa.arc.mct.scenario.component.TagCapability;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JComponent;

public class SummaryView extends View {
	private static final long serialVersionUID = -1683480883187742150L;
	private int MAX_SAMPLES = 2 << 8;
	private Map<String, Summary> costSummaries =
			new HashMap<String, Summary>();
	
	private PieChart chart;
	
	public SummaryView(AbstractComponent ac, ViewInfo vi) {
		super(ac,vi);

		// Default to one-second samples
		double sampleSize = 1000.0;
		DurationCapability dc = ac.getCapability(DurationCapability.class);
		if (dc != null) {
			sampleSize = (double) (dc.getEnd() - dc.getStart()) / (double) MAX_SAMPLES;
			if (sampleSize < 1.0) {
				sampleSize = 1.0;
			}
		}
		
		summarize(ac, sampleSize, new HashSet<String>(), new HashSet<String>());		
		
		List<String> costNames = new ArrayList<String>();
		costNames.addAll(costSummaries.keySet());
		Collections.sort(costNames);
		
		setLayout(new BorderLayout());
		
		Summary summary = costNames.isEmpty() ?
				new Summary("") : costSummaries.get(costNames.get(0));

		chart = new PieChart(summary);
		
		add(chart, BorderLayout.CENTER);
		setBackground(Color.DARK_GRAY);	
	}
		
	
	private void summarize(AbstractComponent ac, double sampleSize, Set<String> tagContext, Set<String> ignore) {
		boolean visitChildren = true;
		Collection<CostFunctionCapability> costs;
		if (ac instanceof CostFunctionComponent) {
			costs = ((CostFunctionComponent) ac).getInternalCostFunctions();
			visitChildren = true;
		} else {
			// Recognize costs from non-CostFunctionComponents, but don't dig deeper
			costs = ac.getCapabilities(CostFunctionCapability.class);
			if (costs != null && !costs.isEmpty()) {
				visitChildren = false;
			}
		}
		Set<String> activeTagContext = tagContext;
		Collection<TagCapability> tagCapabilities = ac.getCapabilities(TagCapability.class);
		if (tagCapabilities != null && !tagCapabilities.isEmpty()) {
			activeTagContext = new HashSet<String>();
			activeTagContext.addAll(tagContext);
			for (TagCapability tag : tagCapabilities) {
				activeTagContext.add(tag.getTag());
			}
		}
		if (costs != null) {
			for (CostFunctionCapability cost : costs) {
				Collection<Long> changeTimes = cost.getChangeTimes();
				if (changeTimes.size() > 1) {
					String name = cost.getName();
					if (!costSummaries.containsKey(name)) {
						costSummaries.put(name, new Summary(name));
					}			
					Iterator<Long> it = changeTimes.iterator();
					long first = it.next();
					long last  = it.next();
					while (it.hasNext()) {
						last = it.next();
					}
					// TODO: Maybe just add getTotalCost to CostFunctionCapability
					for (double t = first; t < last; t += sampleSize) {
						double thisSampleSize = (t + sampleSize) > last ?
								(last - t) : sampleSize;
						double value = cost.getValue((long)(t + thisSampleSize/2)) * thisSampleSize;
						costSummaries.get(name).add(value, activeTagContext);
					}					
				}
			}
		}
		ignore.add(ac.getComponentId());
		if (visitChildren == true) {
			for (AbstractComponent child : ac.getComponents()) {
				if (!ignore.contains(child.getComponentId())) {
					summarize(child, sampleSize, activeTagContext, ignore);
				}
			}
		}
	}
	
	private static class PieChart extends JComponent {
		private static final long serialVersionUID = -3249915607722487317L;
		private List<PieSlice> slices;
		private double total;
		
		public PieChart(Summary summary){
			setSummary(summary);
		}
		
		public void setSummary(Summary summary) {
			slices = new ArrayList<PieSlice>();
			for (Entry<Set<String>, Double> contribution : summary.getContributions().entrySet()) {
				slices.add(new PieSlice(contribution.getKey(), contribution.getValue()));
			}
			Collections.sort(slices);
			this.total = summary.costTotal;
		}
		
		@Override
		public void paintComponent (Graphics g) {
			if (slices != null && !slices.isEmpty()) {
				Arc2D arc2D = null;
				double start = 0;
				int size = Math.min(getWidth(), getHeight()) - 10;
				int x    = getWidth() / 2 - size / 2;
				int y    = getHeight() / 2 - size / 2;
				if (g instanceof Graphics2D) {
					arc2D = new Arc2D.Double(Arc2D.PIE);
					arc2D.setFrame(x, y, size, size);
					((Graphics2D) g).setRenderingHint(
							RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				}
				for (PieSlice slice : slices) {
					double arc = (slice.cost / total) * 360.0;
					if (g instanceof Graphics2D) {
						arc2D.setAngleStart(start);
						arc2D.setAngleExtent(arc);
						g.setColor(ScenarioColorPalette.getColor(slice.string));
						((Graphics2D) g).fill(arc2D);
						g.setColor(Color.BLACK);
						((Graphics2D) g).draw(arc2D);
					} else {
						g.setColor(ScenarioColorPalette.getColor(slice.string));
						g.fillArc(x, y, size, size, (int) start, (int) arc);
						g.setColor(Color.BLACK);
						g.drawArc(x, y, size, size, (int) start, (int) arc);
					}
					start += arc;
				}
				g.setColor(ScenarioColorPalette.getColor("Untagged"));
				g.fillArc(x, y, size, size, (int) start, (int) (360.0 - start));				
			}
		}
		
		private static class PieSlice implements Comparable<PieSlice> {
			private List<String> tags = new ArrayList<String>();
			private String string = "";
			private double cost;
			
			public PieSlice(Collection<String> tags, double cost) {
				this.tags.addAll(tags);
				Collections.sort(this.tags);
				for (String tag : this.tags){
					string += tag + " ";
				}
				this.cost = cost;
			}
			
			public String toString() {
				return string;
			}

			@Override
			public int compareTo(PieSlice other) {
				return string.compareTo(other.toString());
			}
		}
		
	}
	
	private static class Summary {
		private String costName;
		private double costTotal;
		private Map<Set<String>,Set<String>> costSets =
				new HashMap<Set<String>,Set<String>>();
		private Map<Set<String>, Double> contributions = 
				new HashMap<Set<String>, Double>();
		// Note: Set does override equals as expected
		
		public Summary(String costName) {
			this.costName = costName;
			this.costTotal = 0;
		}
		
		public void add(double value, Set<String> tagContext) {
			if (!contributions.containsKey(tagContext)) {
				Set<String> set = new HashSet<String>();
				set.addAll(tagContext);
				contributions.put(set, 0.0);
				costSets.put(set,set);
			}
			contributions.put(costSets.get(tagContext), 
					contributions.get(tagContext) + value);
			costTotal += value;
		}
		
		/**
		 * Create a new summary, only considering a specific set of tags.
		 * @param tags the tags to be considered in the summary
		 * @return a new summary, considering only those tags
		 */
		public Summary subSummary(Set<String> tags) {
			Summary summary = new Summary(costName);
			for (Entry<Set<String>, Double> contribution : contributions.entrySet()) {
				summary.add(contribution.getValue(), intersection(tags, contribution.getKey()));
			}
			return summary;
		}
		
		public Map<Set<String>, Double> getContributions() {
			return contributions;
		}
		
		public double getTotal() {
			return costTotal;
		}
		
		// Utility method - set intersection
		private <T> Set<T> intersection(Set<T> a, Set<T> b) {
			if (a.containsAll(b)) {
				return b;
			} else if (b.containsAll(a)) {
				return a;
			} else {
				Set<T> intersection = new HashSet<T>();
				for (T item : a) {
					if (b.contains(item)) {
						intersection.add(item);
					}
				}
				return intersection;
			}			
		}
	}
	
}
