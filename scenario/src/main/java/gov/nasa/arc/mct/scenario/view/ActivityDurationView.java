package gov.nasa.arc.mct.scenario.view;

import gov.nasa.arc.mct.scenario.component.ActivityComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class ActivityDurationView extends JPanel {

	private static final int TIME_UNIT_PIX = 35;
	private final BasicStroke SOLID_2PT_LINE_STROKE = new BasicStroke(2f);
	private ActivityComponent ac;
	
	private static final long serialVersionUID = 629262372674078697L;
	public ActivityDurationView(ActivityComponent ac) {
		this.ac = ac;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
														RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(renderHints);
		
		Rectangle rectangle = new Rectangle(0, 0, getWidth(), 25);
		int arcWidthAndHeight = TIME_UNIT_PIX/2;				
		g2.setColor(Color.cyan);
		g2.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arcWidthAndHeight, arcWidthAndHeight);
		g2.setStroke(SOLID_2PT_LINE_STROKE);
		g2.setColor(Color.blue);
		g2.drawRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arcWidthAndHeight, arcWidthAndHeight);
		String eventTitle = ac.getDisplayName();
		g2.drawString(eventTitle, rectangle.x + 5, rectangle.y + rectangle.height - 5);

		
	}
}
