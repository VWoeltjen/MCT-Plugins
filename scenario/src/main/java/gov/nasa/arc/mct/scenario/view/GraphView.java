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
package gov.nasa.arc.mct.scenario.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.scenario.component.CostFunctionCapability;
import gov.nasa.arc.mct.scenario.component.DurationCapability;
import gov.nasa.arc.mct.scenario.view.timeline.TimelineLocalControls;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class GraphView extends AbstractTimelineView {
	private static final long serialVersionUID = -2300291952094003401L;

	public static final String VIEW_ROLE_NAME = "Cost Graph";
	public static final ViewInfo VIEW_INFO = 
			new ViewInfo(GraphView.class, GraphView.VIEW_ROLE_NAME, ViewType.EMBEDDED);
	
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private static final int GRAPH_HEIGHT = 60;
	private static final int GRAPH_PAD    = 16;
	
	public GraphView(AbstractComponent ac, ViewInfo vi) {
		super(ac, vi);
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		for (CostFunctionCapability cost : ac.getCapabilities(CostFunctionCapability.class)) {
			getContentPane().add(new CostGraph(cost));
		}
	}
	
	
	private class CostGraph extends JPanel {
		private static final long serialVersionUID = 2939539607481881113L;
		private CostFunctionCapability cost;

		private int x[] = {};
		private int y[] = {};
		private double dataPoints[] = {};

		private int cachedWidth = 0;
		
		public CostGraph(CostFunctionCapability cost) {
			super();
			setOpaque(false);
			this.cost = cost;
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(Box.createVerticalStrut(GRAPH_HEIGHT + GRAPH_PAD * 2));
			updateGraph();
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			// Update graph data for new width
			if (getWidth() != cachedWidth) {
				updateGraph();
				cachedWidth = getWidth();
			}
			
			g.setColor(Color.RED); // TODO: Get from CostFunction ? 
			if (g instanceof Graphics2D) {
				((Graphics2D) g).setStroke(GRAPH_STROKE);
				RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				((Graphics2D) g).setRenderingHints(renderHints);
			}
			
			int charHeight = getFontMetrics(getFont()).getHeight();
			if (x.length > 1 && x.length == y.length) {
				for (int i = 0; i < x.length - 1; i++) {
					g.drawLine(x[i], y[i], x[i+1], y[i]);
					g.drawLine(x[i+1], y[i], x[i+1], y[i+1]);
					
					double maxValue = Math.max(dataPoints[i], dataPoints[i+1]);
					double minValue = Math.min(dataPoints[i], dataPoints[i+1]);
					if (maxValue != minValue) {
						int maxY = Math.min(y[i], y[i+1]);
						int minY = Math.max(y[i], y[i+1]);
						String maxValueString = Double.toString(maxValue);
						g.drawString(maxValueString, x[i+1] - getFontMetrics(getFont()).charsWidth(maxValueString.toCharArray(), 0, maxValueString.length())/2, maxY - charHeight / 4);
						String minValueString = Double.toString(minValue);
						g.drawString(minValueString, x[i+1] - getFontMetrics(getFont()).charsWidth(minValueString.toCharArray(), 0, minValueString.length())/2, minY + charHeight);
					}
				}
			}
		}
		
		private int toX(long t) {
			return (int) (getPixelScale() * (t - getTimeOffset())) + getLeftPadding();
		}
		
		private int toY(double data, double minData, double maxData) {
			return GRAPH_PAD + GRAPH_HEIGHT - (int) (((data - minData) / (maxData - minData)) * (GRAPH_HEIGHT-1)) - 1;
		}
		
		private void updateGraph() {
			Collection<Long> changeTimes = cost.getChangeTimes();
			if (changeTimes.size() > 1) {
				double data[] = new double[changeTimes.size()];
				long   time[] = new long[changeTimes.size()];
				double maxData = Double.NEGATIVE_INFINITY;
				double minData = Double.POSITIVE_INFINITY;
				int i = 0;
				for (Long t : changeTimes) {
					data[i]   = cost.getValue(t);
					if (data[i] > maxData) maxData = data[i];
					if (data[i] < minData) minData = data[i];
					time[i++] = t;
				}				
				x = new int[i];
				y = new int[i];
				dataPoints = new double[i];
				for (int j = 0 ; j < i ; j++) {
					x[j] = toX(time[j]);
					y[j] = toY(data[j], minData, maxData);
					dataPoints[j] = data[j];
				}
			}
		}		
	}
		
}
