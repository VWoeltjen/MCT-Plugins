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
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;
import gov.nasa.arc.mct.components.PropertyEditor;

import java.util.ArrayList;
import java.util.Arrays;
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
public class DynamicActivityTypeComponent extends AbstractActivityTypeComponent {
	private List<CostCapability> internalCosts = 
			Arrays.<CostCapability> asList(
					new CommActivityTypeCost(),
					new DynamicPowerActivityTypeCost()); 
	
	private final static double STANDARD_VOLTAGE = 28.0;

	@Override
	public List<CostCapability> getInternalCosts() {
		return internalCosts;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
	
	public void setCosts(double power, double comms) {
		ActivityTypeModel m = model.get();
		m.setComms(comms);
		m.setPower(power);
	}
	
	// use formula R = V * V / P
	private double getImpedance(double power) {
		return STANDARD_VOLTAGE * STANDARD_VOLTAGE / power;
	}

	private class DynamicPowerActivityTypeCost extends PowerActivityTypeCost {

		@Override
		public String getName() {
			return "Impedence";
		}

		@Override
		public String getUnits() {
			return "Ohm";
		}

	}
}
