/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.scenario.view.timeline;

import gov.nasa.arc.mct.scenario.component.DurationCapability;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class TimelineLocalControls extends JPanel implements DurationCapability {
	public static final int LEFT_MARGIN = 80;
	public static final int RIGHT_MARGIN = 12;
	
	private static final int TICK_AREA_HEIGHT = 40;
	
	public static final DateFormat DURATION_FORMAT = new SimpleDateFormat("HH:mm");
	static {
		DURATION_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	private static final long serialVersionUID = 5844637696012429283L;
	
	private DurationCapability masterDuration;
	private JComponent upperPanel; 
	private JComponent contentPane = new JPanel(new GridLayout(1,1));
	private JComponent lowerPanel;
	private TimelineLocalControls parent = null;
	
	private static final Color EDGE_COLOR = new Color(228, 240, 255);
	
	private JSlider zoomControl;
	private JLabel durationLabel;
	
	public TimelineLocalControls(DurationCapability masterDuration) {
		super(new BorderLayout());
		this.masterDuration = masterDuration;
		
		setOpaque(false);
		add(upperPanel = makeUpperPanel(), BorderLayout.NORTH);
		add(contentPane, BorderLayout.CENTER);
		add(lowerPanel = makeLowerPanel(), BorderLayout.SOUTH);
		
		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				updateAncestor();
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				updateAncestor();
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
				updateAncestor();
			}			
		});
	}
	
	private void updateAncestor() {
		parent = (TimelineLocalControls) 
				SwingUtilities.getAncestorOfClass(TimelineLocalControls.class, this);
		boolean isTopLevelControl = parent == null;
		upperPanel.setVisible(isTopLevelControl);
		lowerPanel.setVisible(isTopLevelControl);
	}

	public JComponent getContentPane() {
		return contentPane;
	}
	
	private JComponent makeUpperPanel() {
		JPanel upperPanel = new JPanel(new BorderLayout());
		
		durationLabel = new JLabel();
		zoomControl = new JSlider();
		zoomControl.setOpaque(false);
		
		upperPanel.add(durationLabel, BorderLayout.WEST);
		upperPanel.add(zoomControl, BorderLayout.EAST);
		upperPanel.setBackground(EDGE_COLOR);
		
		upperPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, EDGE_COLOR.darker()),				
				BorderFactory.createEmptyBorder(4, 4, 4, 4))); //TODO: Move to constant
		
		durationLabel.setText("Total Duration: " + DURATION_FORMAT.format(new Date(masterDuration.getEnd() - masterDuration.getStart())));
		
		return upperPanel;
	}
	
	private JComponent makeLowerPanel() {
		SpringLayout springLayout = new SpringLayout();
		JPanel lowerPanel = new JPanel(springLayout);
		
		JLabel hoursLabel = new JLabel("Hours");
		JPanel tickPanel = new JPanel();
		JButton leftButton = new JButton(new PanIcon(-1));
		leftButton.setOpaque(false);
        leftButton.setContentAreaFilled(false);
        leftButton.setBorder(BorderFactory.createEmptyBorder());
		JButton rightButton = new JButton(new PanIcon(1));
		rightButton.setOpaque(false);
        rightButton.setContentAreaFilled(false);
        rightButton.setBorder(BorderFactory.createEmptyBorder());
		tickPanel.setBackground(Color.ORANGE);
		
		
		lowerPanel.add(hoursLabel);
		lowerPanel.add(tickPanel);
		lowerPanel.add(leftButton);
		lowerPanel.add(rightButton);
		
		springLayout.putConstraint(SpringLayout.WEST, tickPanel, getLeftPadding(), SpringLayout.WEST, lowerPanel);
		springLayout.putConstraint(SpringLayout.EAST, tickPanel, -getRightPadding(), SpringLayout.EAST, lowerPanel);
		
		springLayout.putConstraint(SpringLayout.EAST, leftButton, 0, SpringLayout.WEST, tickPanel);
		springLayout.putConstraint(SpringLayout.WEST, rightButton, 0, SpringLayout.EAST, tickPanel);
		springLayout.putConstraint(SpringLayout.EAST, hoursLabel, 0, SpringLayout.WEST, leftButton);
		
		springLayout.putConstraint(SpringLayout.SOUTH, lowerPanel, TICK_AREA_HEIGHT, SpringLayout.NORTH, lowerPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, tickPanel, 0, SpringLayout.SOUTH, lowerPanel);
		springLayout.putConstraint(SpringLayout.NORTH, tickPanel, 0, SpringLayout.NORTH, lowerPanel);
		
		springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, hoursLabel, 0, SpringLayout.VERTICAL_CENTER, lowerPanel);
		springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, leftButton, 0, SpringLayout.VERTICAL_CENTER, lowerPanel);
		springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, rightButton, 0, SpringLayout.VERTICAL_CENTER, lowerPanel);
		
		return lowerPanel;
	}	
	
	@Override
	public long getStart() {
		return masterDuration.getStart();
	}

	@Override
	public long getEnd() {
		return masterDuration.getEnd();
	}

	@Override
	public void setStart(long start) {
		masterDuration.setStart(start); //TODO: Don't delegate
	}

	@Override
	public void setEnd(long end) {
		masterDuration.setEnd(end); //TODO: Don't delegate
	}
	
	public double getPixelScale() {
		return parent != null ?
				parent.getPixelScale() :
				(double) (getWidth() - getLeftPadding() - getRightPadding()) / 
				(double) (getEnd() - getStart());
	}
	
	public long getTimeOffset() {
		return parent != null ? parent.getTimeOffset() : getStart();
	}
	
	public int getLeftPadding() {
		return TimelineLocalControls.LEFT_MARGIN;
	}
	
	public int getRightPadding() {
		return TimelineLocalControls.RIGHT_MARGIN;
	}
	
	
	private class TickMarkPanel extends JPanel {
		private TickMarkPanel() {
			
		}
	}
	
	private class PanIcon implements Icon {
		private final int sign; 
		
		public PanIcon(int sign) {
			this.sign = sign;
		}
		
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			// Draw a triangle pointing either right or left
			int halfSize = getIconWidth() / 2;
			int fromCenter = halfSize - 2;
			int[] xPts = new int[]{ halfSize - fromCenter * sign, halfSize - fromCenter * sign, halfSize + fromCenter * sign};
			int[] yPts = new int[]{ halfSize - fromCenter, halfSize + fromCenter, halfSize };
			for (int i = 0; i < 3; i++) {
				xPts[i] += x;
				yPts[i] += y;
			}
			g.fillPolygon(xPts, yPts, 3);
		}

		@Override
		public int getIconWidth() {
			return RIGHT_MARGIN;
		}

		@Override
		public int getIconHeight() {
			return RIGHT_MARGIN;
		}
		
	}
}
