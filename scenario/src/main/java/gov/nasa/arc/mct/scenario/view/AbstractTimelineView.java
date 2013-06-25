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


/**
 * Abstract superclass for views which display timeline data. The motivation for 
 * defining a parent class for these is to consolidate usage of TimelineLocalControls, 
 * which should be included for all such views (note that TimelineLocalControls will   
 * suppress its own visibility and derive settings when nested within another 
 * TimelineLocalControls instance.)
 * 
 * TODO: Should also implement 'timeline overlay listener'
 * 
 * @author vwoeltje
 *
 */
public abstract class AbstractTimelineView extends View {
	private static final long serialVersionUID = -5683099761127087080L;

	private TimelineLocalControls timelineContainer;
	
	public AbstractTimelineView(AbstractComponent ac, ViewInfo vi) {
		super(ac,vi);
		
		DurationCapability dc = ac.getCapability(DurationCapability.class) ;
		if (dc != null) {
			timelineContainer = new TimelineLocalControls(dc);
			add(timelineContainer);
		}
	}
	
	public double getPixelScale() {
		return timelineContainer != null ?
				(getWidth() - getLeftPadding() - getRightPadding()) / 
				(double) (timelineContainer.getEnd() - timelineContainer.getStart()) : 
				1.0;
	}
	
	public long getTimeOffset() {
		return timelineContainer != null ? timelineContainer.getStart() : 0;
	}
	
	public int getLeftPadding() {
		return TimelineLocalControls.LEFT_MARGIN;
	}
	
	public int getRightPadding() {
		return TimelineLocalControls.RIGHT_MARGIN;
	}
	

	protected JComponent getContentPane() {
		return timelineContainer != null ? timelineContainer.getContentPane() : this; 
	}
}
