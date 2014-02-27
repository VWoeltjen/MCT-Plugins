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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JPanel;

/**
 * A MultiSlider provides a horizontal control with start and end points that can be adjusted 
 * independently. It is intended for use in adjusting the window upon a timeline. 
 * @author vwoeltje
 *
 */
public class MultiSlider extends JPanel {
	private static final long serialVersionUID = 1754007114546411725L;
	private static final int EDGE_WIDTH = 12;
	private static final int BUTTON_WIDTH  = 8;
	private static final int BUTTON_HEIGHT = 16;
	private static final int TRACK_HEIGHT  = 8;
	private float lowSliderPosition  = 0.0f;
	private float highSliderPosition = 1.0f;

	private Set<ActionListener> actionListeners = new HashSet<ActionListener>();
	private ActionEvent changeEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "change");
	
	/**
	 * Create a new multi-slider
	 */
	public MultiSlider() {
		addMouseListener(new SliderMouseListener());
		add(Box.createVerticalStrut(BUTTON_HEIGHT));
	}
	
	/**
	 * Create a new multi-slider, initialized to a specific state
	 * @param lowSliderPosition the position of the left slider (0.0 - 1.0)
	 * @param highSliderPosition the position of the right slider (0.0 - 1.0)
	 */
	public MultiSlider(float lowSliderPosition, float highSliderPosition) { 
		this();
		this.lowSliderPosition  = lowSliderPosition;
		this.highSliderPosition = highSliderPosition;
	}

	/**
	 * Add an action listener to this component. This listener will be notified 
	 * when slider positions change.
	 * @param actionListener the listener to be notified
	 */
	public void addActionListener(ActionListener actionListener) {
		actionListeners.add(actionListener);
	}
	
	/**
	 * Remove an action listener from this component
	 * @param actionListener the listener to be removed
	 */
	public void removeActionListener(ActionListener actionListener) {
		actionListeners.remove(actionListener);
	}
	
	/**
	 * Gets the low (leftmost) slider position, as a proportion of the total size
	 * (so, a value between 0.0 and 1.0)
	 * @return the position of the low slider (between 0.0 and 1.0)
	 */
	public float getLowProportion() {
		return lowSliderPosition;
	}
	
	/**
	 * Gets the high (rightmost) slider position, as a proportion of the total size
	 * (so, a value between 0.0 and 1.0)
	 * @return the position of the low slider (between 0.0 and 1.0)
	 */
	public float getHighProportion() {
		return highSliderPosition;
	}
	
	/**
	 * Set the low (leftmost) and high (rightmost) slider positions, as 
	 * a proportion of the total size (between 0.0 and 1.0)
	 * @param low the position of the low slider
	 * @param high the position of the high slider
	 */
	public void setSelectedProportions(float low, float high) {
		if (low < high) {
			lowSliderPosition = low;
			highSliderPosition = high;
			repaint();
		}
	}
	
	/**
	 * Get the width allocated to the edges of the slider (that is, 
	 * the number of pixels on each side not allocated to the 
	 * track between minimum and maximum.)
	 * @return number of pixels used on edges
	 */
	public int getEdgeWidth() {
		return EDGE_WIDTH;
	}
	
	/**
	 * Inform action listeners that some action has occurred
	 * @param actionEvent the action to broadcast to this component's listeners
	 */
	protected void fireActionPerformed (ActionEvent actionEvent) {
		for (ActionListener listener : actionListeners) listener.actionPerformed(actionEvent);
	}
	
	@Override
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		if (g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		int w = getWidth() - getEdgeWidth() * 2;
		int x1 = (int) ( w * lowSliderPosition ) + getEdgeWidth();
		int x2 = (int) ( w * highSliderPosition ) + getEdgeWidth();
		int bh = BUTTON_HEIGHT  - 2; // button height
		int th = TRACK_HEIGHT;
		int by = getHeight() / 2 - BUTTON_HEIGHT / 2 + 1;
		int ty = (getHeight() - th) / 2;

		int[] offset = { -1, 1, 0 };
		
		// Draw track
		Color bg = getBackground();
		Color fg = getForeground();
		Color shadow = Color.BLACK;
		Color highlight = Color.WHITE;
		Color[] color = { average(bg, shadow), average(bg, highlight), bg };
		for (int i = 0; i < 3; i++) {
			g.setColor(color[i]);
			g.fillRoundRect(getEdgeWidth() + offset[i], ty + offset[i], w, th, th, th);
		}

		// Fill in active track
		Color active = average(fg,bg); 
		color = new Color[]{ average(active, highlight), average(active, shadow), active };
		for (int i = 0; i < 3; i++) {
			g.setColor(color[i]);
			g.fillRect(x1 + i - 1, ty + offset[i], (x2-x1), th);
		}

		// Draw thumbs
		color = new Color[]{ average(fg, highlight), average(fg, shadow), fg };
		for (int i = 0; i < 3; i++) {
			g.setColor(color[i]);
			g.fillArc(x1 + offset[i] - BUTTON_WIDTH, by + offset[i], BUTTON_WIDTH*2, bh, 90, 180);
			g.fillArc(x2 + offset[i] - BUTTON_WIDTH, by + offset[i], BUTTON_WIDTH*2, bh, 270, 180);
		}		
	}
	
	private Color average(Color a, Color b) {
		return new Color(
				(a.getRed() + b.getRed()) / 2, 
				(a.getGreen() + b.getGreen()) / 2, 
				(a.getBlue() + b.getBlue()) / 2);
	}
	
	private Rectangle getButtonBounds(double position, boolean high) {
		int y = getHeight() / 2;
		int x = EDGE_WIDTH;
		int w = getWidth() - EDGE_WIDTH * 2;
		int x1 = (int) (w * position)  + x;
		int offset = high ? 0 : BUTTON_WIDTH; 
		return new Rectangle(x1 - offset, y - BUTTON_HEIGHT/2, BUTTON_WIDTH, BUTTON_HEIGHT);
	}
	
	
	private Rectangle getTrackBounds(double low, double high) {
		int y = getHeight() / 2;
		int x = EDGE_WIDTH;
		int w = getWidth() - EDGE_WIDTH * 2;
		int x1 = (int) (w * low)  + x;
		int x2 = (int) (w * high) + x;
        return new Rectangle(x1, y - TRACK_HEIGHT/2, x2-x1, TRACK_HEIGHT);
	}
	
	private class SliderMouseListener implements MouseListener {
		int   initialX;
		float initialValue;
		MouseMotionListener mml;

		@Override
		public void mouseClicked(MouseEvent arg0) {}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {
			mml = null;
			initialX = arg0.getX();
			initialValue = lowSliderPosition;
			Rectangle low   = getButtonBounds(lowSliderPosition, false);
			Rectangle high  = getButtonBounds(highSliderPosition, true);
			Rectangle track = getTrackBounds(lowSliderPosition, highSliderPosition);
			final float scale = (float) getTrackBounds(0.0, 1.0).getWidth();
			final float interval = highSliderPosition - lowSliderPosition;

			if (low.contains(arg0.getX(), arg0.getY())) {
				mml = new MouseMotionListener() {
					@Override
					public void mouseDragged(MouseEvent arg0) {
						double delta = (float) (arg0.getX() - initialX) / scale;
						float nextValue = (float) (initialValue + delta);
						if (nextValue < 0.0) nextValue = 0.0f;
						if (nextValue > highSliderPosition) nextValue = highSliderPosition;
						if (lowSliderPosition != nextValue) {
							lowSliderPosition = nextValue;
							MultiSlider.this.repaint();
							fireActionPerformed(changeEvent);
						}
					}
					@Override
					public void mouseMoved(MouseEvent arg0) {}
				};				
			} else if (high.contains(arg0.getX(), arg0.getY())) {
				initialValue = highSliderPosition;
				mml = new MouseMotionListener() {
					@Override
					public void mouseDragged(MouseEvent arg0) {
						double delta = (float) (arg0.getX() - initialX) / scale;
						float nextValue = (float) (initialValue + delta);
						if (nextValue < lowSliderPosition) nextValue = lowSliderPosition;
						if (nextValue > 1.0) nextValue = 1.0f;
						if (highSliderPosition != nextValue) {
							highSliderPosition = nextValue;
							MultiSlider.this.repaint();
							fireActionPerformed(changeEvent);
						}
					}
					@Override
					public void mouseMoved(MouseEvent arg0) {}
				};
			} else if (track.contains(arg0.getX(), arg0.getY())){
				mml = new MouseMotionListener() {
					@Override
					public void mouseDragged(MouseEvent arg0) {
						double delta = (float) (arg0.getX() - initialX) / scale;
						float nextValue = (float) (initialValue + delta);
						if (nextValue < 0.0) nextValue = 0.0f;
						if (nextValue + interval > 1.0f) nextValue = 1.0f - interval;
						if (lowSliderPosition != nextValue) {
							lowSliderPosition = nextValue;
							highSliderPosition = nextValue + interval;
							MultiSlider.this.repaint();
							fireActionPerformed(changeEvent);
						}
					}
					@Override
					public void mouseMoved(MouseEvent arg0) {}
				};				
			}
			if (mml != null)
				MultiSlider.this.addMouseMotionListener(mml);
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if (mml != null)
				MultiSlider.this.removeMouseMotionListener(mml);
			mml = null;
		}
		
	}

}
