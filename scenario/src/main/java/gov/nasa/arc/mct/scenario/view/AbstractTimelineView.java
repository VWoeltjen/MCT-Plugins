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
import gov.nasa.arc.mct.scenario.component.DurationCapability;
import gov.nasa.arc.mct.scenario.view.timeline.TimelineLocalControls;
import gov.nasa.arc.mct.services.component.ViewInfo;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract superclass for views which display timeline data. The motivation for 
 * defining a parent class for these is to consolidate usage of TimelineLocalControls, 
 * which should be included for all such views at the top level. 
 * (these views are often composited; note that TimelineLocalControls will   
 * suppress its own visibility and derive settings when nested within another 
 * TimelineLocalControls instance.)
 * 
 * @author vwoeltje
 *
 */
public abstract class AbstractTimelineView extends View implements ChangeListener {
	private static final long serialVersionUID = -5683099761127087080L;
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTimelineView.class);
	
	private TimelineLocalControls timelineContainer;
	
	public AbstractTimelineView(AbstractComponent ac, ViewInfo vi) {
		super(ac,vi);
		
		// Configure based on DurationCapability
		DurationCapability dc = ac.getCapability(DurationCapability.class) ;
		if (dc != null) {
			timelineContainer = new TimelineLocalControls(dc);
			add(timelineContainer);
			timelineContainer.addChangeListener(this);
		} else {
			LOGGER.warn(getClass().getName() + " instantiated for component without DurationCapability. " +
					"This should have been prevented by policy. Subsequent errors anticipated.");
		}
	}
	
	/**
	 * Get the start time currently displayed, in milliseconds
	 * @return
	 */
	public long getStart() {
		return timelineContainer.getStart();
	}
	
	public long getEnd() {
		return timelineContainer.getEnd();
	}
	
	public double getPixelScale() {
		return timelineContainer != null ?
				timelineContainer.getPixelScale() : 
				1.0;
	}
	
	public long getTimeOffset() {
		return timelineContainer != null ? timelineContainer.getTimeOffset() : 0;
	}
	
	public int getLeftPadding() {
		return timelineContainer.getLeftPadding();
	}
	
	public int getRightPadding() {
		return timelineContainer.getRightPadding();
	}
	

	protected JComponent getContentPane() {
		return timelineContainer != null ? timelineContainer.getContentPane() : this; 
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		revalidate();
		repaint();
	}
	
	
}
