package gov.nasa.arc.mct.scenario.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.scenario.component.ActivityComponent;
import gov.nasa.arc.mct.scenario.component.ActivityData;
import gov.nasa.arc.mct.scenario.component.TimelineComponent;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

@SuppressWarnings("serial")
public final class TimelineView extends View {
	private static final Color TIME_SCALE_COLOR = new Color(44, 170, 208, 155);
	private static final Color[] LINE_COLORS = new Color[] {
		new Color(203, 217, 77, 100), new Color(242, 163, 16, 100)
	};
	private Color durationColor = new Color(200,200,200, 100);
	private static final Color LINE_COLOR = new Color(100, 100, 100);	
	private static final Color TEXT_COLOR = Color.DARK_GRAY;

	public static final String VIEW_ROLE_NAME = "Timeline";
	private static final int TIME_UNIT_PIX = 35;
	private final BasicStroke SOLID_2PT_LINE_STROKE = new BasicStroke(2f);
	private final BasicStroke SOLID_1PT_LINE_STROKE = new BasicStroke(1f);
	private Date globalStartTime = null, globalEndTime = null;
	private int timeScaleIconWidth = 20;
	private int timeScaleHeight = 25;
	private int xStart, xEnd;
	private long pixelMillis;
	private List<ActivityComponent> activities = null;
	private List<Widget> widgets;
	private List<ActivityComponent> topActivities = null;
	private List<TreeMap<Date, Double>> timeseries = new ArrayList<TreeMap<Date,Double>>();

	public TimelineView(AbstractComponent ac, ViewInfo vi) {
		super(ac, vi);
		EditorListener listener = new EditorListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}
	
	private void initActivities() {
		activities = new ArrayList<ActivityComponent>();
		widgets = new ArrayList<TimelineView.Widget>();
		topActivities = new ArrayList<ActivityComponent>();
		addAllActivitiesRecursively(getManifestedComponent());
		addTopLevelActivities(getManifestedComponent());
		
		TreeMap<Date, Double> powerChanges = new TreeMap<Date, Double>();
		TreeMap<Date, Double> commBandwidthChanges = new TreeMap<Date, Double>();
		for (ActivityComponent activityComponent : activities) {
			ActivityData data = activityComponent.getModel().getData();
			Date startTime = data.getStartTime();
			Date endTime = data.getEndTime();
			if (globalStartTime == null) {					
				globalStartTime = startTime;
				globalEndTime = endTime;
			} else {				
				if (startTime.before(globalStartTime))
					globalStartTime = startTime;
				if (endTime.after(globalEndTime))
					globalEndTime = endTime;
			}
			
			// Record power value in dataset
			if (powerChanges.containsKey(data.getStartTime()))
				powerChanges.put(data.getStartTime(), data.getPower() + powerChanges.get(data.getStartTime()));
			else
				powerChanges.put(data.getStartTime(), data.getPower());
			
			if (powerChanges.containsKey(data.getEndTime()))
				powerChanges.put(data.getEndTime(), powerChanges.get(data.getEndTime()) - data.getPower());
			else
				powerChanges.put(data.getEndTime(), (-1) * data.getPower());
			
			// Record comm bandwidth value in dataset
			if (commBandwidthChanges.containsKey(data.getStartTime()))
				commBandwidthChanges.put(data.getStartTime(), data.getComm() + commBandwidthChanges.get(data.getStartTime()));
			else
				commBandwidthChanges.put(data.getStartTime(), data.getComm());
			
			if (commBandwidthChanges.containsKey(data.getEndTime()))
				commBandwidthChanges.put(data.getEndTime(), commBandwidthChanges.get(data.getEndTime()) - data.getComm());
			else
				commBandwidthChanges.put(data.getEndTime(), (-1) * data.getComm());

		}
		pixelMillis = (globalEndTime.getTime() - globalStartTime.getTime()) / (xEnd - xStart);

		double currentValue = 0;
		TreeMap<Date, Double> dataset = new TreeMap<Date, Double>();
		timeseries.add(dataset);
		for (Date date : powerChanges.keySet()) {
			currentValue += powerChanges.get(date);
			dataset.put(date, currentValue);
		}
		
		dataset = new TreeMap<Date, Double>();
		timeseries.add(dataset);
		for (Date date : commBandwidthChanges.keySet()) {
			currentValue += commBandwidthChanges.get(date);
			dataset.put(date, currentValue);
		}

	}

	private void addAllActivitiesRecursively(AbstractComponent ac) {
		for (AbstractComponent c : ac.getComponents()) {
			if (c instanceof ActivityComponent) {
				activities.add((ActivityComponent) c);				
			}
			addAllActivitiesRecursively(c);
		}
	}
	
	private void addTopLevelActivities(AbstractComponent ac) {
		assert ac instanceof TimelineComponent;
		for (AbstractComponent c : ac.getComponents()) {
			if (c instanceof ActivityComponent) {
				topActivities.add((ActivityComponent) c);				
			}
		}		
	}
	
	@Override
	protected void paintComponent(Graphics g) {		
		Graphics2D g2 = (Graphics2D) g;
		g2.clearRect(0, 0, getWidth(), getHeight());
		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
														RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(renderHints);
		
		xStart = timeScaleIconWidth;
		xEnd = getWidth() - timeScaleIconWidth;
		
		paintTimeScale(g2);
		initActivities();
		
		int yStart = 0;
		for (ActivityComponent ac : activities) {
			if (topActivities.iterator().next() != ac && topActivities.contains(ac)) {
				yStart += 15;
				drawActivityDivider(g2, yStart);
			}
			
			paintActivity(g2, ac, yStart);			
			yStart += 50;			
		}
		
		paintTrend(g2, "W", getHeight() - (timeScaleHeight + 15));
	}
	
	private void drawActivityDivider(Graphics2D g2, int yStart) {
		g2.setStroke(SOLID_1PT_LINE_STROKE);
		g2.setColor(TEXT_COLOR);
		g2.drawLine(xStart, yStart, xEnd, yStart);		
	}
	
	private void paintTimeScale(Graphics2D g2) {
		int y1 = getHeight() - timeScaleHeight, y2 = getHeight();
		g2.setColor(TIME_SCALE_COLOR);
		g2.setStroke(SOLID_2PT_LINE_STROKE);
		g2.drawPolygon(new int[]{0, xStart, xStart}, new int[]{y2 - timeScaleHeight / 2, y1, y2}, 3);
		g2.drawPolygon(new int[]{getWidth(), xEnd, xEnd}, new int[]{y2 - timeScaleHeight / 2, y1, y2}, 3);
		g2.setStroke(SOLID_1PT_LINE_STROKE);
		int intervals = (xEnd - xStart) / 5;
		for (int i = 0; i <= intervals; i++)
			g2.drawLine(xStart + i*5, y1, xStart + i*5, y2);		
	}
	
	private void paintActivity(Graphics2D g2, ActivityComponent ac, int yStart) {
		ActivityData data = ac.getModel().getData();		
		if (globalStartTime == null || globalEndTime == null)
			return;
		
		long timeDiff = globalEndTime.getTime() - globalStartTime.getTime();
		int xd = xEnd - xStart;
		int x1 = xStart + (int) ((data.getStartTime().getTime() - globalStartTime.getTime()) * xd / timeDiff);
		int x2 = xStart + (int) ((data.getEndTime().getTime() - globalStartTime.getTime()) * xd / timeDiff);
		
		// Draw activity duration
		int durationWidth = x2 - x1;
		Rectangle rectangle = new Rectangle(x1, yStart + 15, durationWidth, 35);
		Widget widget = new Widget(ac, rectangle);
		widgets.add(widget);
		int arcWidthAndHeight = TIME_UNIT_PIX/2;
		g2.setColor(durationColor);
		g2.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arcWidthAndHeight, arcWidthAndHeight);
		g2.setStroke(SOLID_2PT_LINE_STROKE);
		g2.setColor(LINE_COLOR);
		g2.drawRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arcWidthAndHeight, arcWidthAndHeight);
		String name = getTruncatedString(g2, ac.getDisplayName(), widget);
		int charsWidth = getFontMetrics(getFont()).charsWidth(name.toCharArray(), 0, name.length());
		int charHeight = getFontMetrics(getFont()).getHeight();
		g2.setColor(TEXT_COLOR);
		g2.drawString(name, x1 + durationWidth/2 - charsWidth/2, yStart + 15 + 14 + charHeight/2);						
	}
	
	private void paintTrend(Graphics2D g2, String legend, int yAxisBottom) {
		g2.setStroke(SOLID_2PT_LINE_STROKE);
		g2.setColor(LINE_COLOR);
		// Draw power in trend
		int yAxisLength = 100;
		int yAxisTop = yAxisBottom - yAxisLength;
		// Draw unit
		int charsWidth = getFontMetrics(getFont()).charsWidth(legend.toCharArray(), 0, legend.length());
		int charHeight = getFontMetrics(getFont()).getHeight();
		g2.drawString(legend, xStart - timeScaleIconWidth - charsWidth, yAxisTop + yAxisLength/2 + charHeight/2);
		// Draw Y axis
		g2.drawLine(xStart, yAxisTop, xStart, yAxisTop + yAxisLength);
		g2.drawLine(xStart - 5, yAxisTop, xStart + 5, yAxisTop);
		g2.drawLine(xStart - 5, yAxisTop + yAxisLength/2, xStart + 5, yAxisTop + yAxisLength/2);
		g2.drawLine(xStart - 5, yAxisTop + yAxisLength, xStart + 5, yAxisTop + yAxisLength);
//		// Draw X axis
//		g2.drawLine(xStart, yAxisTop + yAxisLength/2, xEnd, yAxisTop + yAxisLength/2);
		
		// Plot line in steps
		for (TreeMap<Date, Double> dataset : timeseries) {
			if (dataset.isEmpty()) continue;
			g2.setColor(getNextPlotLineColor());
			Iterator<Date> iterator = dataset.keySet().iterator();
			Date date = iterator.next();
			int x1 = translateDateToX(date), x2;
			int y1 = translateValueToY(dataset.get(date), 0, 15, yAxisBottom, yAxisLength), y2;		
			while(iterator.hasNext()) {
				date = iterator.next();
				x2 = translateDateToX(date);
				y2 = translateValueToY(dataset.get(date), 0, 15, yAxisBottom, yAxisLength);
				
				// Plot line in steps
				g2.drawLine(x1, y1, x2, y1);
				g2.drawLine(x2, y1, x2, y2);
				
				x1 = x2;
				y1 = y2;
			}			
		}
	}
	
	private int translateDateToX(Date date) {
		long timeDiff = globalEndTime.getTime() - globalStartTime.getTime();
		int xd = xEnd - xStart;
		int x = xStart + (int) ((date.getTime() - globalStartTime.getTime()) * xd / timeDiff);
//		System.out.println(StartTimePropertyEditor.FORMATTER.format(date) + " x = " + x);
		return x;
	}
	
	private int translateValueToY(double v, double minValue, double maxValue, int yAxisBottom, int yAxisLength) {
		double diff = maxValue - minValue;
		int y = yAxisBottom - (int) ((v - minValue) * yAxisLength / diff);
//		System.out.println("v = " + v + " y = " + y);
		return y;
	}
	
	private static int LINE_COUNTER = 0;
	private static Color getNextPlotLineColor() {		
		Color color = LINE_COLORS[(LINE_COUNTER % LINE_COLORS.length)];
		LINE_COUNTER++;
		return color;		
	}
	
	private static final String ELLIPSIS = "...";
	private String getTruncatedString(Graphics2D g2, String str, Widget widget) {
		int charsWidth = g2.getFontMetrics().charsWidth(str.toCharArray(), 0, str.length());
		int totalTextWidth = widget.rectangle.width - 10;
		
		if (charsWidth <= totalTextWidth)
			return str;
		
		int totalStrLength = totalTextWidth * str.length() / charsWidth;
		return str.substring(0, totalStrLength - 3) + ELLIPSIS;
		

	}

	
	private final class Widget {
		private ActivityComponent activity;
		private Rectangle rectangle;
		public Widget(ActivityComponent ac, Rectangle r) {
			activity = ac;
			rectangle = r;
		}
	}

    private void synchWidgetToManifInfo(Widget widget, Rectangle newRectangle, int cursorType) {
    	ActivityData data = widget.activity.getModel().getData();
		long durationDiff = (widget.rectangle.width - newRectangle.width) * pixelMillis;
    	if (cursorType == Cursor.W_RESIZE_CURSOR) {
    		Date newStartDate = new Date(data.getStartTime().getTime() + durationDiff);
			data.setStartDate(newStartDate);
		} else if (cursorType == Cursor.E_RESIZE_CURSOR) {
    		data.setEndDate(new Date(data.getEndTime().getTime() + durationDiff));
		} else if (cursorType == Cursor.MOVE_CURSOR) {
			assert durationDiff == 0;
			long starttimeDiff = (widget.rectangle.x - newRectangle.x) * pixelMillis;
			data.setStartDate(new Date(data.getStartTime().getTime() + starttimeDiff));
			data.setEndDate(new Date(data.getEndTime().getTime() + starttimeDiff));
		}
    	widget.activity.save();
    }

	private final class EditorListener extends MouseAdapter {
		private Widget targetWidget = null;
		private Point clickPoint = null;
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			clickPoint = arg0.getPoint();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {			
			int cursorType = getCursor().getType();
			if (targetWidget != null) {
				if (cursorType == Cursor.W_RESIZE_CURSOR) {
					Rectangle newRectangle = new Rectangle();
					newRectangle.x = e.getPoint().x;
					newRectangle.width = targetWidget.rectangle.width + (clickPoint.x - e.getPoint().x);  
					synchWidgetToManifInfo(targetWidget, newRectangle, Cursor.W_RESIZE_CURSOR);
				} else if (cursorType == Cursor.E_RESIZE_CURSOR) {
					Rectangle newRectangle = new Rectangle();
					newRectangle.x = targetWidget.rectangle.x;
					newRectangle.width = targetWidget.rectangle.width + (clickPoint.x - e.getPoint().x);  
					synchWidgetToManifInfo(targetWidget, newRectangle, Cursor.E_RESIZE_CURSOR);
				} else if (cursorType == Cursor.MOVE_CURSOR) {
					Rectangle newRectangle = new Rectangle();
					int pixmove = clickPoint.x - e.getPoint().x;
					newRectangle.x = targetWidget.rectangle.x + pixmove;
					newRectangle.width = targetWidget.rectangle.width;  
					synchWidgetToManifInfo(targetWidget, newRectangle, Cursor.MOVE_CURSOR);					
				}
				globalStartTime = globalEndTime = null;
				clickPoint = e.getPoint();
		    	widgets.clear();
		    	activities.clear();
		    	timeseries.clear();
				repaint();
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			clickPoint = null;
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			if (widgets == null) return;
			int x = e.getPoint().x;
			int y = e.getPoint().y;
			for (Widget widget : widgets) {
				int ymax = widget.rectangle.y + widget.rectangle.height;
				if (y >= widget.rectangle.y && y <= ymax) {					
					if (x == widget.rectangle.x) {
						setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
						targetWidget = widget;
						return;
					}
					if (x == (widget.rectangle.x + widget.rectangle.width)) {
						setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
						targetWidget = widget;
						return;
					}
					if (widget.rectangle.contains(e.getPoint())) {
						setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
						targetWidget = widget;
						return;
					}
				}
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			targetWidget = null;
		}
	}
}
