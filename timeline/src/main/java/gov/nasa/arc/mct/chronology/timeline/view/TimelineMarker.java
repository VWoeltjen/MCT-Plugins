package gov.nasa.arc.mct.chronology.timeline.view;

import gov.nasa.arc.mct.chronology.event.ChronologicalInstant;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Displays a set of tick marks spaced horizontally to indicate points in time.
 * @author vwoeltje
 *
 * @param <T> the type of object which describes a specific instant in the appropriate chronology  
 */
public class TimelineMarker<T extends ChronologicalInstant> extends JPanel {
	private static final long serialVersionUID = 7299135724269276393L;
 
	
	private static final int MARKER_WIDTH = 128;
	private static final int TICK_HEIGHT  = 32;
	private static final int TEXT_HEIGHT  = 32;
	private static final int TOTAL_HEIGHT = TEXT_HEIGHT + TICK_HEIGHT;

	private List<Mark> major = new ArrayList<Mark>();
	private List<Mark> minor = new ArrayList<Mark>();
	@SuppressWarnings("unchecked")
	private List<Mark> marks[] = new List[10];
	
	private Alignment align;
	
	public enum Alignment {
		TOP, BOTTOM
	}
	
	/**
	 * Create a new set of tick marks
	 * @param intervalProvider a description of both the displayed and displayable intervals of time
	 * @param width the pixel width this component should occupy
	 * @param a the position - TOP or BOTTOM - at which to draw the tick marks
	 */
	public TimelineMarker(TimelineInterval<T> intervalProvider, 
			int width, Alignment a) {

	    for (int i = 0; i < marks.length; i++) { 
	    	marks[i] = new ArrayList<Mark>();
	    }
	    
		align = a;
	    changeInterval(intervalProvider, width);
	    
	}
	
	/**
	 * Change the interval and/or pixel width for these tick marks
	 * @param intervalProvider a description of both the displayed and displayable intervals of time
	 * @param width the pixel width this component should occupy
	 */
	public void changeInterval(TimelineInterval<T> intervalProvider, int width) {
		//TODO: Just update labels
		removeAll();
		major.clear();
		minor.clear();
		setPreferredSize(new Dimension(width, TOTAL_HEIGHT));
		
	    int count   = width / MARKER_WIDTH;
		if (count <= 0) count = 1;
		
		setLayout(new GridLayout(1, 1));
		
//		List<T> slices = intervalProvider.getDomain().slice(intervalProvider.getInterval(), count);
//		for (T slice : slices) {
//			double p = intervalProvider.getDomain().locateBetween(slice, intervalProvider.getInterval().getStart(), intervalProvider.getInterval().getEnd()); 
//			major.add(new Mark(p, slice.toString()));
//		}
//		
//		slices = intervalProvider.getDomain().slice(intervalProvider.getInterval(), slices.size() * 6);
//		for (T slice : slices) {
//			double p = intervalProvider.getDomain().locateBetween(slice, intervalProvider.getInterval().getStart(), intervalProvider.getInterval().getEnd()); 
//			minor.add(new Mark(p, slice.toString()));
//		}


		for (int i = 0; i < marks.length; i++) {
			marks[i].clear();
			for (T slice : intervalProvider.getDomain().slice(intervalProvider.getInterval(), count * (i+1))) {
				double p = intervalProvider.getDomain().locateBetween(slice, intervalProvider.getInterval().getStart(), intervalProvider.getInterval().getEnd()); 
				marks[i].add(new Mark(p, slice.toString()));				
			}
		}
		
		revalidate();
		repaint();
	}
	
	public void paint(Graphics g) {
		super.paint(g);

		int scale = 1;
		
		for (int i = 0; i < marks.length; i++) {
			if (i > 0 && marks[i].size() == marks[i-1].size())
				continue;
			int h = TICK_HEIGHT / scale;
			int y = align == Alignment.BOTTOM ? 0 : TOTAL_HEIGHT - h;  
			for (Mark m : marks[i]) {
				int x = (int) (getWidth() * m.p);
				g.setColor  (getForeground());
				g.drawLine  (x, y, x, y+h);
				if (i==0) {
					Rectangle bounds = g.getFont().getStringBounds(m.label, g.getFontMetrics().getFontRenderContext()).getBounds();
					int sy = align == Alignment.TOP ? TEXT_HEIGHT / 2 : TOTAL_HEIGHT - TEXT_HEIGHT / 2;
					g.drawString(m.label, x - bounds.width/2, sy + bounds.height/3);
				}
			}
			scale <<= 1;
		}
		
//		for (Mark m : major) {
//			int x = (int) (getWidth() * m.p);
//			g.setColor  (getForeground());
//			g.drawLine  (x, 0, x, MAJOR_HEIGHT);
//			g.drawString(m.label, x, 12 + MAJOR_HEIGHT);
//		}
//		
//		for (Mark m : minor) {
//			int x = (int) (getWidth() * m.p);
//			g.setColor  (getForeground());
//			g.drawLine  (x, 0, x, MINOR_HEIGHT);
//		}
		
	}
	
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}
	
	private class Mark {
		double p;
		String label;
		public Mark(double p, String label) {
			this.p = p;
			this.label = label;
		}
	}
}
