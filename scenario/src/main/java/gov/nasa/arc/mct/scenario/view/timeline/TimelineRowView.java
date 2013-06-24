package gov.nasa.arc.mct.scenario.view.timeline;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.scenario.component.DurationCapability;
import gov.nasa.arc.mct.scenario.view.ActivityView;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimelineRowView extends View {
	private static final int TIMELINE_ROW_HEIGHT = 60;
	private static final long serialVersionUID = -5039383350178424964L;
	private DurationCapability durationProvider;
	private List<JComponent> rows = new ArrayList<JComponent>();
	private JPanel upperPanel = new JPanel();
	
	public TimelineRowView(AbstractComponent ac, ViewInfo vi) {
		super(ac,vi);
		
		// Get the 'top-level' duration for the container
		durationProvider = ac.getCapability(DurationCapability.class);
		if (durationProvider == null) { // fallback to a default
			// TODO: Log, potentially throw exception
			new DurationInfoStub(0,30L*60*1000);
		}
				
		setLayout(new BorderLayout());
		add(upperPanel, BorderLayout.NORTH);
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
		
		// Add all children
		for (AbstractComponent child : ac.getComponents()) {
			addActivities(child, 0, new HashSet<String>());
		}
	}
	
	private void addActivities(AbstractComponent ac, int depth, Set<String> ids) {
		DurationCapability dc = ac.getCapability(DurationCapability.class);
		if (dc != null && !ids.contains(ac.getComponentId())) {
			addViewToRow(dc, ac, depth);
			ids.add(ac.getComponentId()); // Prevent infinite loops in case of cycle
			for (AbstractComponent child : ac.getComponents()) {
				addActivities(child, depth + 1, ids);
			}			
		}
	}
	
	private void addViewToRow(DurationCapability dc, AbstractComponent ac, int row) {
		while (row >= rows.size()) {
			rows.add(new JPanel(new TimelineRowLayout()));
			add(rows.get(rows.size() - 1));
		}
		rows.get(row).add(ActivityView.VIEW_INFO.createView(ac), dc);
	}
	
	public static void main(String[] args) {
		TimelineRowView rowView = new TimelineRowView(null, null);//new DurationInfoStub(0l, 1000l));
		rowView.add(new JLabel("Test"), new DurationInfoStub(10L*1000, 20L*2000));
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(rowView);
		frame.setSize(500, 100);
		frame.setVisible(true);
	}
	
	public double getPixelScale() {
		return getWidth() / (double) (durationProvider.getEnd() - durationProvider.getStart());
	}
	
	public long getTimeOffset() {
		return durationProvider.getStart();
	}

	private class TimelineRowLayout implements LayoutManager2 {
		private Map<Component, DurationCapability> durationInfo = new HashMap<Component, DurationCapability>();
		
		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
			durationInfo.remove(comp);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {			
			return new Dimension(0, TIMELINE_ROW_HEIGHT);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(0, TIMELINE_ROW_HEIGHT);
		}

		@Override
		public void layoutContainer(Container parent) {
			for (Component child : parent.getComponents()) {
				DurationCapability duration = durationInfo.get(child);
				if (duration != null) {
					int x = (int) (getPixelScale() * (duration.getStart() - getTimeOffset()));
					int width = (int) (getPixelScale() * (duration.getEnd() - duration.getStart()));
					child.setBounds(x, 0, width, TIMELINE_ROW_HEIGHT);					
				}
			}
		}

		@Override
		public void addLayoutComponent(Component comp, Object constraints) {
			if (constraints instanceof DurationCapability) {
				durationInfo.put(comp, (DurationCapability) constraints);
			} else {
				throw new IllegalArgumentException("Only valid constraint for TimelineRow is DurationInfo");
			}
		}

		@Override
		public Dimension maximumLayoutSize(Container parent) {
			return new Dimension(parent.getParent().getWidth(), TIMELINE_ROW_HEIGHT);
		}

		@Override
		public float getLayoutAlignmentX(Container target) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getLayoutAlignmentY(Container target) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void invalidateLayout(Container target) {
		}
		
	}
}

class DurationInfoStub implements DurationCapability {
	private long start, end;
	
	public DurationInfoStub(long start, long end) {
		super();
		this.start = start;
		this.end = end;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}
}