package gov.nasa.arc.mct.scenario.view;

import gov.nasa.arc.mct.scenario.component.DurationCapability;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimelineLayout implements LayoutManager2 {
	private int rowHeight = 24;
	private TimelineContext context;
	private Map<Component, DurationCapability> durationInfo = new HashMap<Component, DurationCapability>();
	
	private List<SortedSet<Component>> rows = new ArrayList<SortedSet<Component>>();
	private Map<Component, Integer> rowMap = new HashMap<Component, Integer>();
	
	
	
	public TimelineLayout(TimelineContext context) {
		super();
		this.context = context;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		durationInfo.remove(comp);
		for (int i = 0 ; i < rows.size() ; i++) {
			cleanupRow(i);
		}
		for (int i = 1 ; i < rows.size() ; i++) {
			packRow(i);
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {			
		return new Dimension(1000, getHeight());
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(0, getHeight());
	}

	@Override
	public void layoutContainer(Container parent) {
		// Fix row assignments
		for (int i = 0 ; i < rows.size() ; i++) {
			cleanupRow(i);
		}
		for (int i = 1 ; i < rows.size() ; i++) {
			packRow(i);
		}
		
		// DEBUG: Check rows
		for (int i = 0; i < rows.size(); i++) {
			for (Component c : rows.get(i)) {
				if (rowMap.get(c) != i) {
					System.err.println("???" + i + "," + rowMap.get(c));
				}
			}
		}
		
		// Lay out components temporally
		for (Component child : parent.getComponents()) {
			DurationCapability duration = durationInfo.get(child);
			if (duration != null) {
				int x = context.getLeftPadding() + (int) (context.getPixelScale() * (double) (duration.getStart() - context.getTimeOffset()));
				int width = (int) (context.getPixelScale() * (double) (duration.getEnd() - duration.getStart())) + 1;
				child.setBounds(x, getRow(child) * rowHeight, width, rowHeight);					
			}
		}
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (constraints instanceof DurationCapability) {
			durationInfo.put(comp, (DurationCapability) constraints);
			setRow(comp, 0); // Will get sorted during layout
		} else {
			throw new IllegalArgumentException("Only valid constraint for " + getClass().getName() + 
					" is " + DurationCapability.class.getName());
		}
		for (int i = 0 ; i < rows.size() ; i++) {
			cleanupRow(i);
		}
		for (int i = 1 ; i < rows.size() ; i++) {
			packRow(i);
		}
	}

	@Override
	public Dimension maximumLayoutSize(Container parent) {
		return new Dimension(Integer.MAX_VALUE, getHeight());
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0.5f;
	}

	@Override
	public void invalidateLayout(Container target) {
	}
	
	private int getHeight() {
		return rowHeight * rows.size();
	}
	
	private int getRow(Component comp) {
		Integer row = rowMap.get(comp);
		return row == null ? 0 : row;
	}
	
	private void setRow(Component comp, int row) {
		if (rowMap.containsKey(comp)) {
			rows.get(rowMap.get(comp)).remove(comp);
		}
		rowMap.put(comp, row);
		
		while (rows.size() <= row) {
			rows.add(new TreeSet<Component>(comparator));
		}
		rows.get(row).add(comp);
	}
	
	private List<Component> fixList = new ArrayList<Component>();
	
	private void cleanupRow(int row) {
		Set<Component> active = context.getActiveViews();
		SortedSet<Component> comps = rows.get(row);
		
		// Identify comps which should be pushed down a row
		for (Component c1 : comps) {
			for (Component c2 : comps.headSet(c1)) {
				if (!c1.equals(c2) && overlaps(c1, c2)) {
					if (active.contains(c1)) {
						if (!active.contains(c2)) {
							fixList.add(c2);
						}
					} else {
						fixList.add(c1);
					}
				}
			}
		}
		
		// Push them
		for (Component c : fixList) {
			setRow(c, row + 1);
		}
		
		// Clear out the list to reuse later
		fixList.clear();
	}
	
	/*
	 * We assume cleanupRow has already been called for all rows, 
	 * such that any two components on same row don't overlap
	 */
	private void packRow(int row) {

		Set<Component> active = context.getActiveViews();
		if (row > 0) {
			Set<Component> above  = rows.get(row - 1);
			Set<Component> current = rows.get(row);
			for (Component c1 : current) {
				if (!active.contains(c1)) {
					boolean fits = true;
					for (Component c2 : above) {
						if (c1 != c2 && overlaps(c1,c2)) {
							fits = false;
							break;
						}
					}
					if (fits) {
						fixList.add(c1);
					}
				}
			}
		}
		
		// Pull them up
		for (Component c : fixList) {
			setRow(c, row - 1);
		}	
		
		// Clear out to reuse later
		fixList.clear();
	}
	
	private boolean overlaps(Component c1, Component c2) {
		DurationCapability d1 = durationInfo.get(c1);
		DurationCapability d2 = durationInfo.get(c2);
		if (d1 != null && d2 != null) {
			return (d1.getEnd() > d2.getStart() && d2.getEnd() > d1.getStart());
		}		
		return false;
	}
	
	private final Comparator<Component> comparator = new Comparator<Component>() {
		@Override
		public int compare(Component c1, Component c2) {
			DurationCapability d1 = durationInfo.get(c1);
			DurationCapability d2 = durationInfo.get(c2);
			if (d1 != null && d2 != null) {
				return Long.valueOf(d1.getStart()).compareTo(Long.valueOf(d2.getStart()));
			}
			return 0;
		}
		
	};
	
	public interface TimelineContext {
		public int getLeftPadding();
		public double getPixelScale();
		public long getTimeOffset();
		public Set<Component> getActiveViews();
	}
	
	/* TEST */
	public static void main(String[] args) {
		TimelineLayout layout = new TimelineLayout(null);
		layout.context = layout.mouseAdapter;
		
		final JPanel panel = new JPanel(layout);
		for (int i = 0; i < 20; i++) {
			int start = (int) (Math.random() * 400.0);
			int end   = start + (int) (Math.random() * 60.0);
			DurationThing thing = layout.new DurationThing(start,end);
			panel.add(thing,thing);
		}
		
		final JFrame frame = new JFrame();
		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
//		new Timer(25, new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				panel.revalidate();
//				panel.repaint();
//				frame.pack();
//			}			
//		}).start();
	}
	
	private DurationAdapter mouseAdapter = new DurationAdapter(); 
	
	private class DurationAdapter extends MouseAdapter implements TimelineContext {
		private Set<Component> activeView = new HashSet<Component>();
		
		private int lastX;
		
		@Override
		public void mouseDragged(MouseEvent event) {
			int d = event.getXOnScreen() - lastX;
			//System.out.println(event.getX());
			lastX = event.getXOnScreen();
			Object source = event.getSource();
			if (source instanceof DurationCapability) {
				long s = ((DurationCapability) source).getStart();
				long e = ((DurationCapability) source).getEnd();
				((DurationCapability) source).setStart(s + d);
				((DurationCapability) source).setEnd  (e + d);
			}
			if (source instanceof Component) {
				((Component) source).getParent().invalidate();
				((Component) source).getParent().validate();
				((Component) source).getParent().repaint();
			}
		}

		@Override
		public void mousePressed(MouseEvent event) {
			Object source = event.getSource();
			if (source instanceof Component) {
				lastX = event.getXOnScreen();
				activeView.add((Component) source);
			}
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			activeView.clear();
			Object source = event.getSource();
			if (source instanceof Component) {
				((Component) source).getParent().invalidate();
				((Component) source).getParent().validate();
				((Component) source).getParent().repaint();
			}
		}

		@Override
		public int getLeftPadding() {
			return 0;
		}

		@Override
		public double getPixelScale() {
			return 1.0;
		}

		@Override
		public long getTimeOffset() {		
			return 0;
		}

		@Override
		public Set<Component> getActiveViews() {
			return activeView;
		}		
	};
	
	private class DurationThing extends JLabel implements DurationCapability {
		long start, end;

		public DurationThing(long start, long end) {
			super("THING");
			setBorder(BorderFactory.createLineBorder(Color.BLUE));
			this.start = start;
			this.end = end;
			addMouseListener(mouseAdapter);
			addMouseMotionListener(mouseAdapter);
		}

		@Override
		public long getStart() {
			return start;
		}

		@Override
		public long getEnd() {
			return end;
		}

		@Override
		public void setStart(long start) {
			this.start = start;
		}

		@Override
		public void setEnd(long end) {
			this.end = end;
		}
		
	}
}
