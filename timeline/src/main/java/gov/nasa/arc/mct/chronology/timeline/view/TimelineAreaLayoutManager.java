package gov.nasa.arc.mct.chronology.timeline.view;

import gov.nasa.arc.mct.chronology.ChronologyDomain;
import gov.nasa.arc.mct.chronology.event.ChronologicalInstant;
import gov.nasa.arc.mct.chronology.event.ChronologicalInterval;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;

/**
 * A LayoutManager specifically responsible for managing the horizontal positions of components 
 * with specific start / end times. 
 * @author vwoeltje
 *
 * @param <T> the type of object which describes a specific instant in the appropriate chronology  
 */
public class TimelineAreaLayoutManager<T extends ChronologicalInstant> implements LayoutManager2 {
	private Map<Component, ChronologicalInterval<T>> constraints = 
		new HashMap<Component, ChronologicalInterval<T>>();
	private ChronologyDomain<T> domain;
	private ChronologicalInterval<T> interval;
	private Dimension           size;
	
	/**
	 * Create a new TimelineAreaLayoutManager for a particular domain
	 * @param domain the time domain on which to operate (this supplies to logic used to locate objects)
	 * @param interval the initial interval which corresponds to the left and right edges of the parent component
	 * @param timelineWidth the physical width, in pixels, that the timeline should occupy
	 */
	public TimelineAreaLayoutManager (ChronologyDomain<T> domain,
									  ChronologicalInterval<T> interval,
									  int timelineWidth) {
		this.domain   = domain;
		this.interval = interval; 
		this.size     = new Dimension(timelineWidth, 1200);
	}
	
	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
	}

	@Override
	public void layoutContainer(Container container) {
		Component[] components = container.getComponents();
		
		int[] widthUsed  = new int[components.length];
		int[] rowHeights = new int[components.length];
		int[] rowPos     = new int[components.length];
		
		int[] xPos       = new int[components.length];
		int[] rows       = new int[components.length];
		int[] widths     = new int[components.length];
		
		for (int i = 0; i < components.length; i++) {
			widthUsed[i]  = 0;
			rowHeights[i] = 0;
		}
		
		for (int i = 0; i < components.length; i++) {
			Component comp = components[i];
			comp.doLayout();
			ChronologicalInterval<T> componentInterval = constraints.get(comp);
			double start = domain.locateBetween(componentInterval.getStart(), 
					interval.getStart(), interval.getEnd());
			double end   = domain.locateBetween(componentInterval.getEnd(), 
					interval.getStart(), interval.getEnd());
			if (start < 0.0) start = 0.0; // TODO: Indicate truncation to user?
			if (end   > 1.0) end   = 1.0;
			double width = (end == start) ? 0.20 : end - start;
			
			int x = (int) (start * size.getWidth());
			int w = (int) (width * size.getWidth());
			int y = 0;
			for (int row = 0; row < rowHeights.length; row++) { // Find a row 
				if (x >= widthUsed[row]) {
					y = row;
					break;
				}
			}
			
			if (comp.getPreferredSize().getHeight() >= rowHeights[y]) 
				rowHeights[y] = comp.getPreferredSize().height + 1; 
			widthUsed[y] = x + w;		
			
			xPos[i]   = x;
			rows[i]   = y;
			widths[i] = w;
		}
		
		rowPos[0] = 0; 
		for (int i = 1; i < components.length; i++) {
			rowPos[i] = rowPos[i-1] + rowHeights[i];
		}
		
		for (int i = 0; i < components.length; i++) {
			components[i].setLocation( xPos[i],   rowPos[rows[i]]     );
			components[i].setSize    ( widths[i], rowHeights[rows[i]] * 2 );
		}
		
		size = new Dimension(size.width, rowPos[components.length - 1] + 1);

	}

	@Override
	public Dimension minimumLayoutSize(Container arg0) {
		return size;
	}

	@Override
	public Dimension preferredLayoutSize(Container arg0) {
		return size;
	}

	@Override
	public void removeLayoutComponent(Component arg0) {
		constraints.remove(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addLayoutComponent(Component arg0, Object constraint) {
		ChronologicalInterval<T> interval;
		try {
			interval = (ChronologicalInterval<T>) constraint;
			constraints.put(arg0, interval);
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException("Timeline area layout requires compatible interval constraints.");
		}
	}

	@Override
	public float getLayoutAlignmentX(Container arg0) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container arg0) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container arg0) {
		size = new Dimension(size.width, 600);
	}

	@Override
	public Dimension maximumLayoutSize(Container arg0) {
		return size;
	}


}
