package gov.nasa.arc.mct.chronology.timeline.view;

import gov.nasa.arc.mct.chronology.Chronology;
import gov.nasa.arc.mct.chronology.event.ChronologicalEvent;
import gov.nasa.arc.mct.chronology.event.ChronologicalInstant;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A TimelineArea describes chronological sequence as a displayable component.
 *  
 * @author vwoeltje
 *
 * @param <T> the type of object which describes a specific instant in the appropriate chronology  
 */
public class TimelineArea<T extends ChronologicalInstant> extends JPanel {
	private static final long serialVersionUID = 939210548797405463L;
	private final static Logger LOGGER = LoggerFactory.getLogger(TimelineArea.class);
	
	private final Border HARD_BORDER = BorderFactory.createLineBorder(new Color(128, 120, 64), 2); //new CapsuleBorder(20, 3, false);
	
	private static final int LINE_HEIGHT = 60;

    private Chronology<T>            chronology;
    private TimelineInterval<T>      timelineInterval;
	private TimelineView             view;
	private List<TimelineRow> rows = new ArrayList<TimelineRow>();
	
	/**
	 * Create a new timeline representation of some sequence of events
	 * @param chrono the sequence of events to display
	 * @param interval the interval which should be displayed on-screen
	 * @param extent the interval of time for which to prepare data (may extend off-screen)
	 * @param layout a TimelineLayout responsible for managing the layout of this view
	 */
	public TimelineArea (Chronology<T> chrono, 
			  TimelineInterval<T> ti,
			  TimelineView        view) {
		this.chronology = chrono;		
		this.timelineInterval = ti;
		this.view     = view;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		buildArea();
		
		setOpaque(false);
		setBackground(Color.CYAN);
		
		// Mutable chronologies should accept dragged events
		if (chrono.isMutable()) {
			setDropTarget(new TimelineAreaDropTarget());
		}
	}
	
	private void buildArea() {
		rows = new ArrayList<TimelineRow>();
		removeAll();
		for (ChronologicalEvent<T> event : chronology.getEvents()) {
			if (timelineInterval.getDomain().contains(timelineInterval.getBoundary(), event)) {
				addEvent(event);
			}
		}
	}
	
	private void addEvent(final ChronologicalEvent<T> event) {

		double start = timelineInterval.getDomain().locateBetween(event.getStart(), 
				timelineInterval.getBoundary().getStart(), timelineInterval.getBoundary().getEnd());
		double end   = timelineInterval.getDomain().locateBetween(event.getEnd(), 
				timelineInterval.getBoundary().getStart(), timelineInterval.getBoundary().getEnd());

		final JComponent representation = event.getRepresentation(new Dimension(300, LINE_HEIGHT));
		
		representation.setFont(representation.getFont().deriveFont(10.0f));
		representation.setBackground(new Color(230, 230, 200));
		representation.setBorder(HARD_BORDER);
		if (chronology.isMutable()) {
			representation.setTransferHandler(new TransferHandler() {
				private static final long serialVersionUID = -7101095680976605761L;

				@Override
				public int getSourceActions(JComponent c) {
					return COPY;
				}

				@Override
				protected Transferable createTransferable(JComponent c) {
					return new TimelineTransferable<T>(chronology, event);
				}			
			});
			representation.addMouseMotionListener( new MouseMotionListener() {
				public void mouseDragged(MouseEvent evt) {
					if (true) { //mouse pressed 
						representation.getTransferHandler().exportAsDrag(representation, evt, TransferHandler.COPY);
					}
				}

				@Override
				public void mouseMoved(MouseEvent arg0) {
				}

			});
		}
				
		boolean placed = false;
		for (TimelineRow row : rows) {
			placed = row.addComponentAt(representation, start, end);
			if (placed) break;
		}
		if (!placed) {
			TimelineRow row = new TimelineRow();
			row.addComponentAt(representation, start, end);
			rows.add(row);
			add(row, Component.TOP_ALIGNMENT);
		}
		for (TimelineRow row : rows) row.validate();
		representation.validate();
		for (TimelineRow row : rows) row.validate();
		
	}
	
	private class TimelineRow extends JPanel {
		private static final long serialVersionUID = -3967481096524053400L;

		private double maximumX;
		private TimelineLayout timelineLayout = view.getLayout();

		TimelineRow() {
			maximumX = Double.MIN_VALUE;
			setLayout(timelineLayout);
			setBackground(Color.GRAY.darker());
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
		}
		
		public boolean addComponentAt(JComponent comp, double low, double high) {
			if (low < maximumX - 0.000001) return false; // Allow a small epsilon of error
			if (low != high) maximumX = Math.max(maximumX, high);
			else             maximumX = Math.max(maximumX, high + 0.25);
			add(comp, timelineLayout.makeWeight(low, high));
            
			return true;
		}
				
	}
	
	private class TimelineAreaDropTarget extends DropTarget {
		private static final long serialVersionUID = 778939854728776289L;
		
		public void drop (DropTargetDropEvent event) {
			if (event.getTransferable().isDataFlavorSupported(TimelineTransferable.EVENT_FLAVOR)) {
				
				try {
					TimelineTransferable<?> transferable =
						(TimelineTransferable<?>) event.getTransferable().getTransferData(TimelineTransferable.EVENT_FLAVOR);
					
					double proportion = event.getLocation().getX() / TimelineArea.this.getWidth();
					T instant = timelineInterval.getDomain().instantAt(timelineInterval.getInterval(), proportion);
					
					if (!handleTransfer(transferable, instant)) {
						event.rejectDrop();
					}

					view.updateMonitoredGUI();
					
				} catch (Exception ioe) {
					event.rejectDrop();
					LOGGER.warn("Drop event appeared valid but could not be completed due to unexpected exception.", ioe);
					return; 
				}
			} else {
				event.rejectDrop();
				return;
			}
		}
		
		@SuppressWarnings("unchecked")
		private <I extends ChronologicalInstant> boolean handleTransfer(TimelineTransferable<I> transferable, T instant) {
			if (chronology.getDomain().equals(transferable.getChronology().getDomain())) {
				Chronology <I> destination = (Chronology <I>) chronology;
				Chronology <I> source      = transferable.getChronology();
				I              inst        = (I) instant;
				if (destination.add(transferable.getEvent(), inst)) {
					if (!source.remove(transferable.getEvent())) {
						LOGGER.warn("Could not remove source event from its original chronology when moving.");
					} 
					return true;
				}
			}
			return false;
		}
		
	}
	
}
