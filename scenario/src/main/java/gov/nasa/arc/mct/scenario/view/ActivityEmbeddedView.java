package gov.nasa.arc.mct.scenario.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

@SuppressWarnings("serial")
public final class ActivityEmbeddedView extends View {
	public static final String VIEW_ROLE_NAME = "Overview";
	private static final int TIME_UNIT_PIX = 35;
	private final BasicStroke SOLID_2PT_LINE_STROKE = new BasicStroke(2f);
	private Color textColor = Color.white;
	private Color lineColor = new Color(100, 100, 100);
	private Color durationColor = new Color(200,200,200, 100);

	public ActivityEmbeddedView(AbstractComponent ac, ViewInfo vi) {
		super(ac, vi);
		setPreferredSize(new Dimension(300, 50));
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
														RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(renderHints);
		
//		// Draw background
//		g2.setColor(new Color(44, 170, 208, 50));
//		int vlines = getWidth() / 5;
//		for (int i = 0; i <= vlines; i++)
//			g2.drawLine(i*5, 0, i*5, getHeight());
//
//		int hlines = getHeight() / 5;
//		for (int i = 0; i <= hlines; i++)
//			g2.drawLine(0, i*5, getWidth(), i*5);

		int dataStartX = 75, leftMargin = 15, rightMargin = 30, topMargin = 35;
		
		// Draw activity duration
		int durationWidth = getWidth() - dataStartX - leftMargin - rightMargin;
		Rectangle rectangle = new Rectangle(leftMargin + dataStartX, topMargin + 15, durationWidth, 35);
		int arcWidthAndHeight = TIME_UNIT_PIX/2;				
		g2.setColor(durationColor);
		g2.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arcWidthAndHeight, arcWidthAndHeight);
		g2.setStroke(SOLID_2PT_LINE_STROKE);
		g2.setColor(lineColor);
		g2.drawRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arcWidthAndHeight, arcWidthAndHeight);
		String name = getManifestedComponent().getDisplayName();
		int charsWidth = getFontMetrics(getFont()).charsWidth(name.toCharArray(), 0, name.length());
		int charHeight = getFontMetrics(getFont()).getHeight();
		g2.setColor(textColor);
		g2.drawString(name, dataStartX + durationWidth/2 - charsWidth/2, topMargin + 15 + 14 + charHeight/2);				
	}
}
