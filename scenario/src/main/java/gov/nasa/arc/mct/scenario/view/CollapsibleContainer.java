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

import gov.nasa.arc.mct.gui.View;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A collapsible Swing container for an MCT view. Includes a little triangle 
 * to control the visibility of the view beneath.
 * 
 * @author vwoeltje
 *
 */
public class CollapsibleContainer extends JPanel {
	private static final long serialVersionUID = -7397365143392342779L;
	
	private View view;
	private Component label;

	/**
	 * Create a new collapsible container for the specified view. 
	 * This will be labeled with the view's name.
	 * @param view
	 */
	public CollapsibleContainer(View view) {
		this(view, new JLabel(view.getInfo().getViewName()));
	}
	
	/**
	 * Create a new collapsible container for the specified view; 
	 * this will be labeled by the provided Swing component.
	 * @param view
	 * @param label
	 */
	public CollapsibleContainer(View view, Component label) {
		this.view = view;
		this.label = label;
		
		setLayout(new BorderLayout());
		add(view, BorderLayout.CENTER);
		add(createTopPanel(), BorderLayout.NORTH);
		
		setOpaque(false);
	}
	
	private JComponent createTopPanel() {
		JPanel panel = new JPanel();
		JLabel twister = new JLabel(new TwistIcon());
		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());
		
		JPanel leftPanel = new JPanel();
		leftPanel.setOpaque(false);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
		leftPanel.add(Box.createHorizontalStrut(8));
		leftPanel.add(twister);
		leftPanel.add(label);
		twister.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				view.setVisible(!view.isVisible());
			}			
		});
		panel.add(leftPanel, BorderLayout.WEST);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setOpaque(false);
		if (view instanceof GraphView) addGraphButtons(rightPanel);
		panel.add(rightPanel, BorderLayout.EAST);
		
		return panel;
	}
	
	private void addGraphButtons(JPanel panel) {
		final GraphView graphView = (GraphView) view;
		
		if (graphView.hasInstantaneousGraph()) {
			JToggleButton instantButton = new JToggleButton("Instantanious Graph", true);
			instantButton.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent event) {
					AbstractButton abstractButton = (AbstractButton) event.getSource();
				    ButtonModel buttonModel = abstractButton.getModel();
				    boolean selected = buttonModel.isSelected();
				    if (selected) {
				    	graphView.setInstantanious(true);
				    	// graphView.rebuild();
				    }
				    else { graphView.setInstantanious(false); }
				    graphView.rebuild();
				}
				
			});
			panel.add(instantButton);
		}
		
		if (graphView.hasAccumulativeGraph()) {
			JToggleButton accumulativeButton = new JToggleButton("Accumulative Graph", true);	
			accumulativeButton.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent event) {
					AbstractButton abstractButton = (AbstractButton) event.getSource();
				    ButtonModel buttonModel = abstractButton.getModel();
				    boolean selected = buttonModel.isSelected();
				    if (selected) {
				    	graphView.setAccumulative(true);
				    	// graphView.rebuild();
				    }
				    else { graphView.setAccumulative(false); }
				    graphView.rebuild();
				}
				
			});
			panel.add(accumulativeButton); 
		}
		
		
		/** instantButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				AbstractButton button = (AbstractButton) event.getSource();
				boolean selected = button.getModel().isSelected();
				graphView.setInstantanious(selected);
				graphView.setAccumulative(!selected);
				if (selected) {
					graphView.rebuild();
					System.out.println("press instantanious button");
				}								
			}			
		});
		accumulativeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				AbstractButton button = (AbstractButton) event.getSource();
				boolean selected = button.getModel().isSelected();
				graphView.setAccumulative(selected);
				graphView.setInstantanious(!selected);
				if (selected) {
					graphView.rebuild();
					System.out.println("press accumulative button");
				}								
			}			
		}); */
		
		
		
		
		/** ButtonGroup group = new ButtonGroup();
		group.add(instantButton);
		group.add(accumulativeButton); */
		
		
		
		
	}
	
	private class TwistIcon implements Icon {
		private final int size;
	
		public TwistIcon() {
			size = 10;
		}
		
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			// Draw a triangle pointing either right or left
			int halfSize = getIconWidth() / 2;
			int fromCenter = halfSize - 2;
			int[] xPts = new int[]{ halfSize - fromCenter, halfSize - fromCenter, halfSize + fromCenter};
			int[] yPts = new int[]{ halfSize - fromCenter, halfSize + fromCenter, halfSize };
			for (int i = 0; i < 3; i++) {
				xPts[i] += x;
				yPts[i] += y;
			}
			if (!view.isVisible()) {
				g.fillPolygon(xPts, yPts, 3);
			} else {
				g.fillPolygon(yPts, xPts, 3);
			}
		}

		@Override
		public int getIconWidth() {
			return size;
		}

		@Override
		public int getIconHeight() {
			return size;
		}
		
	}

}
