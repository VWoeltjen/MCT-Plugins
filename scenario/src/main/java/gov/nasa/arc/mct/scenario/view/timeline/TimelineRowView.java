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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.scenario.component.DurationCapability;
import gov.nasa.arc.mct.scenario.view.AbstractTimelineView;
import gov.nasa.arc.mct.scenario.view.ActivityView;
import gov.nasa.arc.mct.scenario.view.GraphView;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimelineRowView extends AbstractTimelineView {
	private static final int TIMELINE_ROW_HEIGHT = 40;
	private static final int TIMELINE_ROW_SPACING = 8;
	private static final long serialVersionUID = -5039383350178424964L;

	private List<JComponent> rows = new ArrayList<JComponent>();
	private JPanel upperPanel = new JPanel();
	private Color backgroundColor = Color.WHITE;
	
	
	public TimelineRowView(AbstractComponent ac, ViewInfo vi) {
		super(ac,vi);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(upperPanel, BorderLayout.NORTH);
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
		upperPanel.setOpaque(false);
		upperPanel.add(Box.createVerticalStrut(TIMELINE_ROW_SPACING));
		
		getContentPane().setBackground(backgroundColor);
		
		// Add all children
		for (AbstractComponent child : ac.getComponents()) {
			addActivities(child, 0, new HashSet<String>());
		}
		
		upperPanel.add(GraphView.VIEW_INFO.createView(ac));
	}

	private void addActivities(AbstractComponent ac, int depth, Set<String> ids) {
		DurationCapability dc = ac.getCapability(DurationCapability.class);
		if (dc != null && !ids.contains(ac.getComponentId())) {
			addViewToRow(dc, ac, depth);
			ids.add(ac.getComponentId()); // Prevent infinite loops in case of cycle
			for (AbstractComponent child : ac.getComponents()) {
				addActivities(child, depth + 1, ids);
			}			
		}
	}
	
	private void addViewToRow(DurationCapability dc, AbstractComponent ac, int row) {
		while (row >= rows.size()) {
			rows.add(new JPanel(new TimelineRowLayout()));
			rows.get(rows.size() - 1).setBackground(Color.BLUE);
			rows.get(rows.size() - 1).setOpaque(false);
			upperPanel.add(rows.get(rows.size() - 1));
			upperPanel.add(Box.createVerticalStrut(TIMELINE_ROW_SPACING));
		}
		View activityView = ActivityView.VIEW_INFO.createView(ac);
		MouseAdapter controller = new TimelineDurationController(dc, this);
		rows.get(row).add(activityView, dc);
		activityView.addMouseListener(controller);
		activityView.addMouseMotionListener(controller);
	}
	
	public static void main(String[] args) {
		TimelineRowView rowView = new TimelineRowView(null, null);//new DurationInfoStub(0l, 1000l));
		rowView.add(new JLabel("Test"), new DurationInfoStub(10L*1000, 20L*2000));
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(rowView);
		frame.setSize(500, 100);
		frame.setVisible(true);
	}
	

	private class TimelineRowLayout implements LayoutManager2 {
		private Map<Component, DurationCapability> durationInfo = new HashMap<Component, DurationCapability>();
		
		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
			durationInfo.remove(comp);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {			
			return new Dimension(0, TIMELINE_ROW_HEIGHT);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(0, TIMELINE_ROW_HEIGHT);
		}

		@Override
		public void layoutContainer(Container parent) {
			for (Component child : parent.getComponents()) {
				DurationCapability duration = durationInfo.get(child);
				if (duration != null) {
					int x = getLeftPadding() + (int) (getPixelScale() * (duration.getStart() - getTimeOffset()));
					int width = (int) (getPixelScale() * (duration.getEnd() - duration.getStart())) + 1;
					child.setBounds(x, 0, width, TIMELINE_ROW_HEIGHT);					
				}
			}
		}

		@Override
		public void addLayoutComponent(Component comp, Object constraints) {
			if (constraints instanceof DurationCapability) {
				durationInfo.put(comp, (DurationCapability) constraints);
			} else {
				throw new IllegalArgumentException("Only valid constraint for TimelineRow is DurationInfo");
			}
		}

		@Override
		public Dimension maximumLayoutSize(Container parent) {
			return new Dimension(parent.getParent().getWidth(), TIMELINE_ROW_HEIGHT);
		}

		@Override
		public float getLayoutAlignmentX(Container target) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getLayoutAlignmentY(Container target) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void invalidateLayout(Container target) {
		}
		
	}

}

class DurationInfoStub implements DurationCapability {
	private long start, end;
	
	public DurationInfoStub(long start, long end) {
		super();
		this.start = start;
		this.end = end;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}
}