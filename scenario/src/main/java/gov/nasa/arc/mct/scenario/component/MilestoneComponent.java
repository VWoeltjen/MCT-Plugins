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
package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.concurrent.atomic.AtomicReference;

public class MilestoneComponent extends AbstractComponent implements DurationCapability{
	private AtomicReference<MilestoneModel> model =
			new AtomicReference<MilestoneModel>(new MilestoneModel());
	
	@Override
	public long getStart() {
		return model.get().timestamp;
	}

	@Override
	public long getEnd() {
		return model.get().timestamp;
	}

	@Override
	public void setStart(long start) {
		model.get().timestamp = start;
	}

	@Override
	public void setEnd(long end) {
		model.get().timestamp = end;
	}

	public static class MilestoneModel {
		private long timestamp;
	}
}
