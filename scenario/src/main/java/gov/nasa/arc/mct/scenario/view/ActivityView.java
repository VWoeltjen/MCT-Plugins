package gov.nasa.arc.mct.scenario.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ActivityView extends View {
	private static final long serialVersionUID = -3208388859058655187L;
	public static final String VIEW_ROLE_NAME = "Activity";
	public static final ViewInfo VIEW_INFO = 
			new ViewInfo(ActivityView.class, ActivityView.VIEW_ROLE_NAME, ViewType.EMBEDDED);
	private final BasicStroke SOLID_2PT_LINE_STROKE = new BasicStroke(2f);
	private Color textColor = Color.white;
	private Color lineColor = new Color(100, 100, 100);
	private Color durationColor = new Color(200,200,200, 100);
	
	public ActivityView(AbstractComponent ac, ViewInfo vi) {
		super(ac,vi);
		setOpaque(false);
	}
	
	public void paintComponent(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D) g;
			
			RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
															RenderingHints.VALUE_ANTIALIAS_ON);
			renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHints(renderHints);
			
			// Draw activity duration
			g2.setColor(durationColor);
			g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, getHeight() / 4, getHeight() / 4);
			g2.setStroke(SOLID_2PT_LINE_STROKE);
			g2.setColor(lineColor);
			g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, getHeight() / 4, getHeight() / 4);
			String name = getManifestedComponent().getDisplayName();
			int charsWidth = getFontMetrics(getFont()).charsWidth(name.toCharArray(), 0, name.length());
			int charHeight = getFontMetrics(getFont()).getHeight();
			int baseline   = getFontMetrics(getFont()).getAscent();
			g2.setColor(textColor);
			g2.drawString(name, getWidth() / 2 - charsWidth / 2, baseline + getHeight() / 2 - charHeight / 2);
		}
	}
	
}
