package gov.nasa.arc.mct.scenario.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.scenario.component.ActivityComponent;
import gov.nasa.arc.mct.scenario.component.ActivityData;
import gov.nasa.arc.mct.scenario.component.DecisionComponent;
import gov.nasa.arc.mct.scenario.component.DecisionData;
import gov.nasa.arc.mct.scenario.component.TimelineComponent;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

@SuppressWarnings("serial")
public final class TimelineView extends View {
	private static String DATE_FORMAT = "yyyy/D HH:mm";
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_FORMAT);
	private static final Color TIME_SCALE_COLOR = new Color(44, 170, 208, 155);
	private static final Color[] LINE_COLORS = new Color[] {
		new Color(203, 217, 77), new Color(242, 163, 16)
	};
	private Color DECISION_COLOR = new Color(185,59,143, 100);
	private Color DURATION_COLOR = new Color(200,200,200, 100);
	private static final Color LINE_COLOR = new Color(100, 100, 100);	
	private static final Color TEXT_COLOR = Color.DARK_GRAY;

	public static final String VIEW_ROLE_NAME = "Timeline";
	private static final int TIME_UNIT_PIX = 35;
	private final BasicStroke SOLID_2PT_LINE_STROKE = new BasicStroke(2f);
	private final BasicStroke SOLID_1PT_LINE_STROKE = new BasicStroke(1f);
	private final BasicStroke DASHED_LINE_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {2.0f}, 0.0f);
	private Date globalStartTime = null, globalEndTime = null;
	private int timeScaleIconWidth = 20;
	private int timeScaleHeight = 25;
	private int margin = 30;
	private int xStart, xEnd;
	private long pixelMillis;
	private List<ActivityComponent> activities = null;
	private List<ActivityWidget> activityWidgets;
	private List<DecisionWidget> decisionWidgets;
	private List<TreeMap<Date, Double>> timeseries = new ArrayList<TreeMap<Date,Double>>();
	private List<List<ActivityWrapper>> activityMap;
	private List<DecisionWrapper> decisionMap;
	private int currentTickerX = -1;
	private boolean showVeriticalTickLine = false;

	public TimelineView(AbstractComponent ac, ViewInfo vi) {
		super(ac, vi);
		EditorListener listener = new EditorListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}
	
	private void cleanup() {
		globalStartTime = globalEndTime = null;
    	activityWidgets.clear();
    	decisionWidgets.clear();
    	activities.clear();
    	timeseries.clear();
    	activityMap.clear();
	}
	
	private boolean activitiesInitialized() {
		return activities != null && !activities.isEmpty();
		
	}
	private void initActivities() {
		if (activitiesInitialized())
			return;
		activities = new ArrayList<ActivityComponent>();
		decisionMap = new ArrayList<DecisionWrapper>();
		activityWidgets = new ArrayList<TimelineView.ActivityWidget>();
		decisionWidgets = new ArrayList<TimelineView.DecisionWidget>();
		addAllActivitiesAndDecisionsRecursively(getManifestedComponent());
		ActivityWrapper root = new ActivityWrapper(null);
		replicateActivityTree(getManifestedComponent(), root);
		assignLevel(root);
		activityMap = new ArrayList<List<ActivityWrapper>>();
		populateActivityMap(root);
		assignLevelsForDecisions();
					
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

	private void addAllActivitiesAndDecisionsRecursively(AbstractComponent ac) {
		for (AbstractComponent c : ac.getComponents()) {
			if (c instanceof ActivityComponent) {
				activities.add((ActivityComponent) c);				
			} else if (c instanceof DecisionComponent)
				if (ac instanceof ActivityComponent)
					decisionMap.add(new DecisionWrapper((DecisionComponent) c, (ActivityComponent) ac));
			
			addAllActivitiesAndDecisionsRecursively(c);
		}
	}
	
	private void replicateActivityTree(AbstractComponent ac, ActivityWrapper node) {
		for (AbstractComponent c : ac.getComponents()) {
			if (c instanceof ActivityComponent) {
				ActivityWrapper wrapper = new ActivityWrapper((ActivityComponent) c);
				node.children.add(wrapper);
				replicateActivityTree(c, wrapper);
			}			
		}
	}
	
	private void populateActivityMap(ActivityWrapper node) {
		if (node.activityComponent == null) {
			for (ActivityWrapper top : node.children) {
				List<ActivityWrapper> list = new ArrayList<TimelineView.ActivityWrapper>(), result = new ArrayList<TimelineView.ActivityWrapper>();
				list.add(top);				
				flatten(list, result);
				activityMap.add(result);
			}
		}
		
	}
	
	private void flatten(List<ActivityWrapper> working, List<ActivityWrapper> result) {
		if (working.isEmpty())
			return;
		result.addAll(working);
		List<ActivityWrapper> workingCopy = new ArrayList<TimelineView.ActivityWrapper>();
		for (ActivityWrapper node : working) {
			workingCopy.add(node);
		}
		working.clear();
		for (ActivityWrapper node : workingCopy) {
			working.addAll(node.children);			
		}		
		flatten(working, result);
		
	}
	
	private List<DecisionWrapper> getDecisions(int group, int level) {
		List<DecisionWrapper> d = new ArrayList<TimelineView.DecisionWrapper>();
		for (DecisionWrapper dw : decisionMap) {
			if (dw.group == group && dw.level == level) {
				d.add(dw);
			}
		}
		return d;
	}
	
	@SuppressWarnings("unused")
	private void printActivityMap() {
		for (int level = 0; level < activityMap.size(); level++) {
			System.out.print("Group " + level + ": ");
			for (ActivityWrapper wrapper : activityMap.get(level)) {
				System.out.print(wrapper.activityComponent.getDisplayName() + "[" + wrapper.level + "] " );
			}
			System.out.println();
		}
	}
	
	private void assignLevel(ActivityWrapper node) {
		for (ActivityWrapper child : node.children) {
			assignLevel(child);
		}		
		if (node.children.isEmpty())
			node.level = 0;
		else {
			int maxLevel = 0;
			for (ActivityWrapper child : node.children) {
				if (child.level > maxLevel)
					maxLevel = child.level;
			}
			node.level = maxLevel + 1;
			// re-adjust level
			for (ActivityWrapper child : node.children) {
				child.level = node.level - 1;
			}
		}
	}
	
	private void assignLevelsForDecisions() {
		for (DecisionWrapper dw : decisionMap) {
			assignDecisionLevel(dw);
		}
	}
	
	private void assignDecisionLevel(DecisionWrapper dw) {
		for (int group = 0; group < activityMap.size(); group++) {
			List<ActivityWrapper> list = activityMap.get(group);
			for (ActivityWrapper aw : list) {
				if (dw.parentActivity == aw.activityComponent) {
					dw.group = group;
					dw.level = aw.level - 1;
				}				
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
		
		xStart = timeScaleIconWidth + margin;
		xEnd = getWidth() - timeScaleIconWidth - margin;
		if (currentTickerX == -1)
			currentTickerX = xStart;
		
		paintTimeScale(g2);
		initActivities();
		
		g2.drawString("START: " + FORMATTER.format(globalStartTime), 0, getFontMetrics(getFont()).getHeight());
		String endtimeString = "END: " + FORMATTER.format(globalEndTime);
		g2.drawString(endtimeString, getWidth() - getFontMetrics(getFont()).charsWidth(endtimeString.toCharArray(), 0, endtimeString.length()), getFontMetrics(getFont()).getHeight());
		long hoursDiff = (globalEndTime.getTime() - globalStartTime.getTime()) / (60 * 60 * 1000) % 24;
		long minDiff = (globalEndTime.getTime() - globalStartTime.getTime()) / (60 * 1000) % 60;
		String totalDurationText = "Total Duration: " + String.format("%2s", Long.toString(hoursDiff)).replace(' ', '0') + ":" +  String.format("%2s", Long.toString(minDiff)).replace(' ', '0');
		g2.drawString(totalDurationText, getWidth() - getFontMetrics(getFont()).charsWidth(totalDurationText.toCharArray(), 0, totalDurationText.length()), getFontMetrics(getFont()).getHeight() * 2);
		
		int yStart = 20;
		activityWidgets.clear();
		for (int group = 0; group < activityMap.size(); group++) {
			List<ActivityWrapper> list = activityMap.get(group);
			int level = list.get(0).level;
			for (ActivityWrapper aw : list) {
				if (level != aw.level) {
					for (DecisionWrapper dw : getDecisions(group, level)) {
						paintDecision(g2, dw, yStart);
					}
					yStart += 50;
					level = aw.level;
				}
				paintActivity(g2, aw.activityComponent, yStart);			
			}
			yStart += 65;
			drawActivityDivider(g2, yStart);
		}
		
		paintTrend(g2, "Power (W)", getHeight() - (2*timeScaleHeight + 15), timeseries.get(0));
		paintTrend(g2, "Comm (Kb/s)", getHeight() - (2*timeScaleHeight + 75), timeseries.get(1));
		
		if (showVeriticalTickLine && currentTickerX >= xStart)
			paintVeriticalTickLine(g2);
	}
	
	private void drawActivityDivider(Graphics2D g2, int yStart) {
		g2.setStroke(SOLID_1PT_LINE_STROKE);
		g2.setColor(TEXT_COLOR);
		g2.drawLine(xStart, yStart, xEnd, yStart);		
	}
	
	private void paintTimeScale(Graphics2D g2) {
		int y1 = getHeight() - 2*timeScaleHeight, y2 = getHeight() - timeScaleHeight;
		g2.setColor(TIME_SCALE_COLOR);
		g2.setStroke(SOLID_2PT_LINE_STROKE);
		g2.drawPolygon(new int[]{margin, xStart, xStart}, new int[]{y2 - timeScaleHeight / 2, y1, y2}, 3);
		g2.drawPolygon(new int[]{getWidth() - margin, xEnd, xEnd}, new int[]{y2 - timeScaleHeight / 2, y1, y2}, 3);
		g2.setStroke(SOLID_1PT_LINE_STROKE);
		int intervals = (xEnd - xStart) / 5;
		for (int i = 0; i <= intervals; i++)
			g2.drawLine(xStart + i*5, y1, xStart + i*5, y2);
		paintCurrentTicker(g2);
	}
	
	private void paintCurrentTicker(Graphics2D g2) {
		Polygon polygon = getCurrentTicketPolygon();
		g2.fillPolygon(polygon);		
	}
	
	private void paintVeriticalTickLine(Graphics2D g2) {
		g2.setColor(TIME_SCALE_COLOR);
		g2.drawLine(currentTickerX, getHeight() - timeScaleHeight, currentTickerX, 0);
		int charHeight = getFontMetrics(getFont()).getHeight();
		double sumOfPower = 0, sumOfComm = 0;
		for (ActivityWidget w : activityWidgets) {
			if (w.rectangle.contains(new Point(currentTickerX, w.rectangle.y + w.rectangle.height/2))) {
				g2.setColor(TIME_SCALE_COLOR);
				double power = w.activity.getModel().getData().getPower();
				double comm = w.activity.getModel().getData().getComm();
				sumOfPower += power;
				sumOfComm += comm;
				String text = "Power=" + Double.toString(power) + "W; Comm=" + Double.toString(comm) + "Kb/s";
				int charsWidth = getFontMetrics(getFont()).charsWidth(text.toCharArray(), 0, text.length());
				g2.fillRect(currentTickerX, w.rectangle.y + w.rectangle.height/2 - charHeight/2, charsWidth + 6, charHeight + 6);
				g2.setColor(Color.white);
				g2.drawString(text, currentTickerX + 3, w.rectangle.y + w.rectangle.height/2 + charHeight/2);				
			}
		}
		g2.setColor(TIME_SCALE_COLOR);
		int yAxisBottom = getHeight() - (2*timeScaleHeight + 15);
		int yAxisLength = 50;
		int yAxisTop = yAxisBottom - yAxisLength;
		String text = "Power=" + Double.toString(sumOfPower) + "W";
		int charsWidth = getFontMetrics(getFont()).charsWidth(text.toCharArray(), 0, text.length());
		g2.fillRect(currentTickerX, yAxisTop + yAxisLength/2 - charHeight/2, charsWidth + 6, charHeight + 6);		
		g2.setColor(Color.white);
		g2.drawString(text, currentTickerX + 3, yAxisTop + yAxisLength/2 + charHeight/2);				

		g2.setColor(TIME_SCALE_COLOR);
		yAxisBottom = getHeight() - (2*timeScaleHeight + 75);		
		yAxisTop = yAxisBottom - yAxisLength;		
		text = "Comm=" + Double.toString(sumOfComm) + "Kb/s";
		charsWidth = getFontMetrics(getFont()).charsWidth(text.toCharArray(), 0, text.length());
		g2.fillRect(currentTickerX, yAxisTop + yAxisLength/2 - charHeight/2, charsWidth + 6, charHeight + 6);		
		g2.setColor(Color.white);
		g2.drawString(text, currentTickerX + 3, yAxisTop + yAxisLength/2 + charHeight/2);				

		g2.setColor(TIME_SCALE_COLOR);
		yAxisBottom = getHeight() - timeScaleHeight;
		yAxisTop = yAxisBottom - timeScaleHeight;
		yAxisLength = timeScaleHeight;
		long timeDiff = (currentTickerX - xStart) * (globalEndTime.getTime() - globalStartTime.getTime()) / (xEnd - xStart);
		text = FORMATTER.format(new Date(globalStartTime.getTime() + timeDiff)); 
		charsWidth = getFontMetrics(getFont()).charsWidth(text.toCharArray(), 0, text.length());
		g2.fillRect(currentTickerX, yAxisTop + yAxisLength/2 - charHeight/2, charsWidth + 6, charHeight + 6);		
		g2.setColor(Color.white);
		g2.drawString(text, currentTickerX + 3, yAxisTop + yAxisLength/2 + charHeight/2);				
	}
	
	private Polygon getCurrentTicketPolygon() {
		Polygon polygon = new Polygon(new int[]{currentTickerX - 6, currentTickerX, currentTickerX + 6, currentTickerX + 6, currentTickerX - 6}, 
									  new int[]{getHeight() - timeScaleHeight/2, getHeight() - timeScaleHeight, getHeight() - timeScaleHeight/2, getHeight(), getHeight()}, 5);
		return polygon;
	}
	private boolean tickerMarkPressed(Point p) {
		return getCurrentTicketPolygon().contains(p);
	}
	
	private void paintDecision(Graphics2D g2, DecisionWrapper dw, int yStart) {
		int x1 = translateDateToX(dw.decisionComponent.getModel().getData().getStartTime());
		int x2 = translateDateToX(dw.decisionComponent.getModel().getData().getEndTime());
		int y1 = yStart + 5;
		int y2 = y1 + 10;
		g2.setColor(DECISION_COLOR);
		Polygon p1 = new Polygon(new int[]{x1 - 6, x1, x1 + 6}, new int[]{y1, y2, y1}, 3);
		Polygon p2 = new Polygon(new int[]{x2 - 6, x2, x2 + 6}, new int[]{y1, y2, y1}, 3);
		g2.fillPolygon(p1);
		g2.fillPolygon(p2);
		g2.setStroke(DASHED_LINE_STROKE);
		g2.drawLine(x1, y2, x2, y2);
		decisionWidgets.add(new DecisionWidget(dw.decisionComponent, p1, DecisionMode.START));
		decisionWidgets.add(new DecisionWidget(dw.decisionComponent, p2, DecisionMode.END));
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
		ActivityWidget widget = new ActivityWidget(ac, rectangle);
		activityWidgets.add(widget);
		int arcWidthAndHeight = TIME_UNIT_PIX/2;
		g2.setColor(DURATION_COLOR);
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
	
	private void paintTrend(Graphics2D g2, String legend, int yAxisBottom, TreeMap<Date, Double> dataset) {
		g2.setStroke(SOLID_2PT_LINE_STROKE);
		g2.setColor(LINE_COLOR);
		// Draw power in trend
		int yAxisLength = 50;
		int yAxisTop = yAxisBottom - yAxisLength;
		// Draw unit
		int charsWidth = getFontMetrics(getFont()).charsWidth(legend.toCharArray(), 0, legend.length());
		int charHeight = getFontMetrics(getFont()).getHeight();
		g2.setColor(TEXT_COLOR);
		g2.drawString(legend, xEnd - charsWidth - 5, yAxisTop + yAxisLength);
		// Draw Y axis
		g2.drawLine(xStart, yAxisTop, xStart, yAxisTop + yAxisLength);
		g2.drawLine(xStart - 5, yAxisTop, xStart + 5, yAxisTop);
		g2.drawLine(xStart - 5, yAxisTop + yAxisLength/2, xStart + 5, yAxisTop + yAxisLength/2);
		g2.drawLine(xStart - 5, yAxisTop + yAxisLength, xStart + 5, yAxisTop + yAxisLength);
		// Get min and max in dataset
		double minValue = Double.MAX_VALUE, maxValue = Double.MIN_VALUE;		
		for (double v : dataset.values()) {
			if (v < minValue)
				minValue = v;
			if (v > maxValue)
				maxValue = v;
		}
		String maxValueString = Double.toString(maxValue);
		g2.drawString(maxValueString, xStart - 10 - getFontMetrics(getFont()).charsWidth(maxValueString.toCharArray(), 0, maxValueString.length()), yAxisTop + charHeight/2);
		String minValueString = Double.toString(minValue);
		g2.drawString(minValueString, xStart - 10 - getFontMetrics(getFont()).charsWidth(minValueString.toCharArray(), 0, minValueString.length()), yAxisBottom + charHeight/2);
				
		// Plot line in steps
		if (dataset.isEmpty()) return;
		g2.setColor(getNextPlotLineColor());
		Iterator<Date> iterator = dataset.keySet().iterator();
		Date date = iterator.next();
		int x1 = translateDateToX(date), x2;
		int y1 = translateValueToY(dataset.get(date), minValue, maxValue, yAxisBottom, yAxisLength), y2;		
		while(iterator.hasNext()) {
			date = iterator.next();
			x2 = translateDateToX(date);
			y2 = translateValueToY(dataset.get(date), minValue, maxValue, yAxisBottom, yAxisLength);
			
			// Plot line in steps
			g2.drawLine(x1, y1, x2, y1);
			g2.drawLine(x2, y1, x2, y2);
			
			x1 = x2;
			y1 = y2;
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
	private static final String EM_STRING = "";
	private String getTruncatedString(Graphics2D g2, String str, ActivityWidget widget) {
		int charsWidth = g2.getFontMetrics().charsWidth(str.toCharArray(), 0, str.length());
		int totalTextWidth = widget.rectangle.width - 10;
		
		if (charsWidth <= totalTextWidth)
			return str;
		
		int totalStrLength = totalTextWidth * str.length() / charsWidth;
		if (totalStrLength > 3) 
			return str.substring(0, totalStrLength - 3) + ELLIPSIS;
		else
			return EM_STRING;
	}
	
	private final class ActivityWidget {
		private ActivityComponent activity;
		private Rectangle rectangle;
		public ActivityWidget(ActivityComponent ac, Rectangle r) {
			activity = ac;
			rectangle = r;
		}
	}
	
	private final class DecisionWidget {
		private DecisionComponent decision;
		private Polygon polygon;
		private DecisionMode mode;
		public DecisionWidget(DecisionComponent dc, Polygon p, DecisionMode m) {
			decision = dc;
			polygon = p;
			mode = m;
		}
	}
	
	private enum DecisionMode {START, END};

	private final class EditorListener extends MouseAdapter {
		private ActivityWidget targetActivityWidget = null;
		private DecisionWidget targetDecisionWidget = null;
		private Point clickPoint = null;
		private boolean invalidateActivitySelection = false;
		private boolean invalidateDecisionSelection = false;
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			clickPoint = arg0.getPoint();
			
			if (tickerMarkPressed(clickPoint)) {
				showVeriticalTickLine = true;		
				repaint();
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {			
			int cursorType = getCursor().getType();
			updateTargetActivityWidget();
			updateTargetDecisionWidget();
			if (targetActivityWidget != null) {
				if (cursorType == Cursor.W_RESIZE_CURSOR) {
					Rectangle newRectangle = new Rectangle();
					newRectangle.x = e.getPoint().x;
					newRectangle.width = targetActivityWidget.rectangle.width + (clickPoint.x - e.getPoint().x);  
					synchActivityWidgetToManifInfo(targetActivityWidget, newRectangle, Cursor.W_RESIZE_CURSOR);
				} else if (cursorType == Cursor.E_RESIZE_CURSOR) {
					Rectangle newRectangle = new Rectangle();
					newRectangle.x = targetActivityWidget.rectangle.x;
					newRectangle.width = targetActivityWidget.rectangle.width + (clickPoint.x - e.getPoint().x);
					synchActivityWidgetToManifInfo(targetActivityWidget, newRectangle, Cursor.E_RESIZE_CURSOR);
				} else if (cursorType == Cursor.MOVE_CURSOR) {
					Rectangle newRectangle = new Rectangle();
					int pixmove = clickPoint.x - e.getPoint().x;
					newRectangle.x = targetActivityWidget.rectangle.x + pixmove;
					newRectangle.width = targetActivityWidget.rectangle.width;  
					synchActivityWidgetToManifInfo(targetActivityWidget, newRectangle, Cursor.MOVE_CURSOR);
				}				
				clickPoint = e.getPoint();
			} else if (targetDecisionWidget != null) {
				if (cursorType == Cursor.MOVE_CURSOR) {					
					synchDecisionWidgetToManifInfo(targetDecisionWidget, e.getPoint().x - clickPoint.x);
					clickPoint = e.getPoint();
				}
			}
			if (showVeriticalTickLine) {
				if (e.getPoint().x >= xStart && e.getPoint().x <= xEnd) {
					currentTickerX = e.getPoint().x;
					repaint();
				}
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			clickPoint = null;
			showVeriticalTickLine = false;
			repaint();
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			if (activityWidgets == null) return;
			int x = e.getPoint().x;
			int y = e.getPoint().y;
			for (ActivityWidget widget : activityWidgets) {
				int ymax = widget.rectangle.y + widget.rectangle.height;
				if (y >= widget.rectangle.y && y <= ymax) {					
					if (x == widget.rectangle.x) {
						setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
						targetActivityWidget = widget;
						return;
					}
					if (x == (widget.rectangle.x + widget.rectangle.width)) {
						setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
						targetActivityWidget = widget;
						return;
					}
					if (widget.rectangle.contains(e.getPoint())) {
						setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
						targetActivityWidget = widget;
						return;
					}
				}
			}
			for (DecisionWidget dw : decisionWidgets) {
				if (dw.polygon.contains(e.getPoint())) {
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					targetDecisionWidget = dw;
					return;
				}
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			targetActivityWidget = null;
		}
		
		private void updateTargetActivityWidget() {
			if (!invalidateActivitySelection || targetActivityWidget == null) return;
			for (ActivityWidget aw : activityWidgets) {
				if (targetActivityWidget.activity == aw.activity) {
					targetActivityWidget = aw;
					invalidateActivitySelection = false;
					return;
				}				
			}
		}

		private void updateTargetDecisionWidget() {
			if (!invalidateDecisionSelection || targetDecisionWidget == null) return;
			for (DecisionWidget dw : decisionWidgets) {
				if (targetDecisionWidget.decision == dw.decision && targetDecisionWidget.mode == dw.mode) {
					targetDecisionWidget = dw;
					invalidateDecisionSelection = false;
					return;
				}				
			}
		}

	    private void synchActivityWidgetToManifInfo(ActivityWidget widget, Rectangle newRectangle, int cursorType) {
	    	ActivityData data = widget.activity.getModel().getData();
			long durationDiff = (widget.rectangle.width - newRectangle.width) * pixelMillis;
	    	if (cursorType == Cursor.W_RESIZE_CURSOR) {
	    		Date newStartDate = new Date(data.getStartTime().getTime() + durationDiff);
				if (newStartDate.after(data.getEndTime()))
					return;
				data.setStartDate(newStartDate);
			} else if (cursorType == Cursor.E_RESIZE_CURSOR) {
	    		Date newEndDate = new Date(data.getEndTime().getTime() + durationDiff);
	    		if (newEndDate.before(data.getStartTime()))
	    			return;
	    		data.setEndDate(newEndDate);
			} else if (cursorType == Cursor.MOVE_CURSOR) {
				assert durationDiff == 0;
				long starttimeDiff = (widget.rectangle.x - newRectangle.x) * pixelMillis;
				data.setStartDate(new Date(data.getStartTime().getTime() + starttimeDiff));
				data.setEndDate(new Date(data.getEndTime().getTime() + starttimeDiff));
			}
	    	widget.activity.save();
	    	((TimelineComponent) getManifestedComponent()).addToModifiedObjects(widget.activity);
	    	
			cleanup();
			repaint();
			invalidateActivitySelection = true;
	    }
	    
	    private void synchDecisionWidgetToManifInfo(DecisionWidget widget, int moveDistance) {
	    	DecisionData data = widget.decision.getModel().getData();
			long durationDiff = moveDistance * pixelMillis;
			if (widget.mode == DecisionMode.START) {
				Date newStartDate = new Date(data.getStartTime().getTime() + durationDiff);
				if (newStartDate.after(data.getEndTime()))
					return;
				data.setStartDate(newStartDate);
			} else {
				Date newEndDate = new Date(data.getEndTime().getTime() + durationDiff);
				if (newEndDate.before(data.getStartTime()))
					return;
				data.setEndDate(newEndDate);
			}
	    	widget.decision.save();
	    	((TimelineComponent) getManifestedComponent()).addToModifiedObjects(widget.decision);
	    	invalidateDecisionSelection = true;
	    	
			cleanup();
			repaint();
	    }
	}

	private final class ActivityWrapper {
		private ActivityComponent activityComponent;
		private int level;
		private List<ActivityWrapper> children;
		
		public ActivityWrapper(ActivityComponent activityComponent) {
			this.activityComponent = activityComponent;
			children = new ArrayList<TimelineView.ActivityWrapper>();
			level = 0;
		}
	}
	
	private final class DecisionWrapper {
		private DecisionComponent decisionComponent;
		private ActivityComponent parentActivity;
		private int group, level;
		public DecisionWrapper(DecisionComponent c, ActivityComponent parentActivity) {
			decisionComponent = c;
			this.parentActivity = parentActivity;
			group = level = -1;
		}
	}
}
