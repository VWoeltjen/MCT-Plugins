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

import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An Activity Type exhibits reusable properties of an activity, 
 * such as power draw and comms usage.
 * 
 * @author vwoeltje
 *
 */
public class ActivityTypeComponent extends CostFunctionComponent {
	private AtomicReference<ActivityTypeModel> model = 
			new AtomicReference<ActivityTypeModel>(new ActivityTypeModel());
	private List<CostFunctionCapability> internalCostFunctions = 
			Arrays.<CostFunctionCapability> asList(
					new ActivityTypeCost(false),
					new ActivityTypeCost(true));

	@Override
	public List<CostFunctionCapability> getInternalCostFunctions() {
		return internalCostFunctions;
	}

	@Override
	public <T> T handleGetCapability(Class<T> capability) {
		if (capability.isAssignableFrom(ModelStatePersistence.class)) {
			JAXBModelStatePersistence<ActivityTypeModel> persistence = 
					new JAXBModelStatePersistence<ActivityTypeModel>() {
				@Override
				protected ActivityTypeModel getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(ActivityTypeModel modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<ActivityTypeModel> getJAXBClass() {
					return ActivityTypeModel.class;
				}
			};

			return capability.cast(persistence);
		}
		return null;
	}

	private class ActivityTypeCost implements CostFunctionCapability {
		private boolean isComm;

		public ActivityTypeCost(boolean isComm) {
			this.isComm = isComm;
		}

		@Override
		public String getName() {
			return isComm ? "Comms" : "Power";
		}

		@Override
		public String getUnits() {
			return isComm ? "KB/s" : "Watts";
		}

		@Override
		public double getValue(long time) {
			ActivityTypeModel m = model.get();
			return isComm ? m.getComms() : m.getPower();
		}

		@Override
		public Collection<Long> getChangeTimes() {
			return Collections.emptyList();
		}

	}
}
