package gov.nasa.arc.mct.chronology.timeline.view;

import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.roles.events.PropertyChangeEvent;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import gov.nasa.arc.mct.chronology.event.UNIXTimeInstant;

public class TimelinePlotManager {
	private TimelineInterval<UNIXTimeInstant> interval;
	private List<PlotInfo>   plots = new ArrayList<PlotInfo>();
	
	public TimelinePlotManager(TimelineInterval<UNIXTimeInstant> interval) {
		this.interval = interval;
	}
	
	public void addPlot(View plot) {
		plots.add(new PlotInfo(plot));
	}
	
	public void removePlot(View plot) {
		PlotInfo target = null;
		for (PlotInfo info : plots) {
			if (info.plot == plot) target = info;
		}
		if (target != null) plots.remove(target);
	}
	
	public void update() {
		for (PlotInfo p : plots) p.update();
	}
	
	private class PlotInfo {
		View      plot;
		Component contents;
		int       width = 0;
		int       observations = 0;
		UNIXTimeInstant start, end;
		static final int TOLERANCE = 4;
		
		public PlotInfo(View plot) {
			this.plot = plot;
			this.contents = findContents(plot);
			
			start = interval.getInterval().getStart();
			end   = interval.getInterval().getEnd();
		}
		
		public void update() {	
			// Definitely update if start & end have changed
			if (UNIXTimeInstant.DOMAIN.getComparator().compare(start, interval.getInterval().getStart()) != 0 ||
				UNIXTimeInstant.DOMAIN.getComparator().compare(end,   interval.getInterval().getEnd()  ) != 0) {
				performUpdate();
				return;
			}
			
			if (contents == null) return;
			if (!contents.isValid()) contents = findContents(plot);
			if (contents == null) return;
			if (contents.getWidth() == width) return; // Already up-to-date
			
			if (observations++ < TOLERANCE)   return;
			observations = 0;
			
			performUpdate();
		}
		
		private void performUpdate() {
			width = contents.getWidth();
			double offset = 1.0 - (double) width / (double) plot.getWidth();
			
			start = interval.getInterval().getStart();
			end   = interval.getInterval().getEnd();
			
			UNIXTimeInstant adjustedStart = UNIXTimeInstant.DOMAIN.instantAt(interval.getInterval(), offset);
			
			if (adjustedStart.getTimeMillis() >= end.getTimeMillis()) {
				adjustedStart = new UNIXTimeInstant(end.getTimeMillis() - 1);
			}
			
			plot.getViewProperties().setProperty("TimeMin", 
					Long.toString(adjustedStart.getTimeMillis()));
			plot.getViewProperties().setProperty("TimeMax", 
					Long.toString(end.getTimeMillis()));
			plot.updateMonitoredGUI(new PropertyChangeEvent(plot.getManifestedComponent()));			
		}
		
		private Component findContents(JComponent comp) {
			if (isContents(comp)) return comp;
			for (Component c : comp.getComponents()) {
				if (c instanceof JComponent) {
					Component candidate = findContents((JComponent) c);
					if (candidate != null) return candidate;
				}
			}
			return null;
		}
		
		private boolean isContents(JComponent comp) { 
			// TODO: Is there a better way to achieve this?
			return comp.getClass().getSimpleName().contains("PlotContents");
		}
		
	}
}
