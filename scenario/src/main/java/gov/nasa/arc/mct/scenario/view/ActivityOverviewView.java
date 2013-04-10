package gov.nasa.arc.mct.scenario.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;

@SuppressWarnings("serial")
public final class ActivityOverviewView extends View {
	public static final String VIEW_ROLE_NAME = "Overview";
	private static final int TIME_UNIT_PIX = 35;
	private final BasicStroke SOLID_2PT_LINE_STROKE = new BasicStroke(2f);
	private Color textColor = new Color(100, 100, 100);
	private Color durationColor = new Color(200,200,200, 100);

	public ActivityOverviewView(AbstractComponent ac, ViewInfo vi) {
		super(ac, vi);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
														RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(renderHints);
		
		int dataStartX = 75, leftMargin = 15, rightMargin = 30, topMargin = 15, rowHeight = 75, rowMargin = 10, columnMargin = 10;
		
		// Draw activity duration
		int durationWidth = getWidth() - dataStartX - leftMargin - rightMargin;
		Rectangle rectangle = new Rectangle(leftMargin + dataStartX, topMargin + 15, durationWidth, 50);
		int arcWidthAndHeight = TIME_UNIT_PIX/2;				
		g2.setColor(durationColor);
		g2.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arcWidthAndHeight, arcWidthAndHeight);
		g2.setStroke(SOLID_2PT_LINE_STROKE);
		g2.setColor(textColor);
		g2.drawRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arcWidthAndHeight, arcWidthAndHeight);
		String name = getManifestedComponent().getDisplayName();
		int charsWidth = getFontMetrics(getFont()).charsWidth(name.toCharArray(), 0, name.length());
		int charHeight = getFontMetrics(getFont()).getHeight();
		g2.drawString(name, dataStartX + durationWidth/2 - charsWidth/2, topMargin + 15 + 25 + charHeight/2);
				
		// Draw power in trend
		int xAxisStart = leftMargin + dataStartX - rowMargin;
		int yAxisTop = topMargin + rowHeight + rowMargin;
		int yAxisLength = 55;
		// Draw unit
		String unit = "W";
		charsWidth = getFontMetrics(getFont()).charsWidth(unit.toCharArray(), 0, unit.length());
		g2.drawString(unit, xAxisStart - columnMargin - charsWidth, yAxisTop + yAxisLength/2 + charHeight/2);
		// Draw axis
		g2.drawLine(xAxisStart, yAxisTop, leftMargin + dataStartX - 10, yAxisTop + yAxisLength);
		g2.drawLine(xAxisStart - 5, yAxisTop, xAxisStart + 5, yAxisTop);
		g2.drawLine(xAxisStart - 5, yAxisTop + yAxisLength/2, xAxisStart + 5, yAxisTop + yAxisLength/2);
		g2.drawLine(xAxisStart - 5, yAxisTop + yAxisLength, xAxisStart + 5, yAxisTop + yAxisLength);
		// Draw data trend
		g2.drawLine(dataStartX + 20, yAxisTop + yAxisLength/2, dataStartX + 20 + getWidth() - dataStartX - leftMargin - rightMargin, yAxisTop + yAxisLength/2);

		// Draw comm in trend
		yAxisTop = topMargin + rowHeight * 2 + rowMargin*2;
		// Draw unit
		unit = "Kb/s";
		charsWidth = getFontMetrics(getFont()).charsWidth(unit.toCharArray(), 0, unit.length());
		g2.drawString(unit, xAxisStart - columnMargin - charsWidth, yAxisTop + yAxisLength/2 + charHeight/2);
		// Draw axis
		g2.drawLine(xAxisStart, yAxisTop, leftMargin + dataStartX - 10, yAxisTop + yAxisLength);
		g2.drawLine(xAxisStart - 5, yAxisTop, xAxisStart + 5, yAxisTop);
		g2.drawLine(xAxisStart - 5, yAxisTop + yAxisLength/2, xAxisStart + 5, yAxisTop + yAxisLength/2);
		g2.drawLine(xAxisStart - 5, yAxisTop + yAxisLength, xAxisStart + 5, yAxisTop + yAxisLength);
		// Draw data trend
		g2.drawLine(dataStartX + 20, yAxisTop + yAxisLength/2, dataStartX + 20 + getWidth() - dataStartX - leftMargin - rightMargin, yAxisTop + yAxisLength/2);

	}
}
