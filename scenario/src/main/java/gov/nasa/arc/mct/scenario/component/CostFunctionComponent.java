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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import gov.nasa.arc.mct.components.AbstractComponent;

public abstract class CostFunctionComponent extends AbstractComponent {
	protected List<CostFunctionCapability> getInternalCostFunctions() {
		return Collections.emptyList();
	}
	
	// TODO: Hoist this into a shared superclass of Timeline, Activity, and Scenario?
	@Override
	protected <T> List<T> handleGetCapabilities(Class<T> capability) {
		if (capability.isAssignableFrom(CostFunctionCapability.class)) {
			Map<String, AggregateCostFunction> costFunctions = new HashMap<String, AggregateCostFunction>();
			for (CostFunctionCapability costFunction : getInternalCostFunctions()) {
				if (!costFunctions.containsKey(costFunction.getName())) {
					costFunctions.put(costFunction.getName(), 
							new AggregateCostFunction(costFunction.getName(), costFunction.getUnits()));
				}
				costFunctions.get(costFunction.getName()).add(costFunction);
			}
			for (AbstractComponent child : getComponents()) {
				for (CostFunctionCapability costFunction : child.getCapabilities(CostFunctionCapability.class)) {
					if (!costFunctions.containsKey(costFunction.getName())) {
						costFunctions.put(costFunction.getName(), 
								new AggregateCostFunction(costFunction.getName(), costFunction.getUnits()));
					}
					costFunctions.get(costFunction.getName()).add(costFunction);
				}
			}
			
			// Assemble into a nice list
			List<T> aggregateCostFunctions = new ArrayList<T>();
			for (AggregateCostFunction aggregateCostFunction : costFunctions.values()) {
				aggregateCostFunctions.add(capability.cast(aggregateCostFunction));
			}
			
			return aggregateCostFunctions;
		}
		return super.handleGetCapabilities(capability);
	}

	private class AggregateCostFunction implements CostFunctionCapability {
		private String name;
		private String units;
		private List<CostFunctionCapability> costs = new ArrayList<CostFunctionCapability>();
		
		public AggregateCostFunction(String name, String units) {
			super();
			this.name = name;
			this.units = units;
		}

		void add(CostFunctionCapability cost) {
			costs.add(cost);
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getUnits() {
			return units;
		}

		@Override
		public double getValue(long time) {
			double sum = 0;
			for (CostFunctionCapability c : costs) {
				sum += c.getValue(time);
				// TODO: Could exit this loop early, but data sets may never be large enough that this matters...
			}
			return sum;
		}

		@Override
		public Collection<Long> getChangeTimes() {
			Collection<Long> changeTimes = new TreeSet<Long>();
			for (CostFunctionCapability c : costs) {
				changeTimes.addAll(c.getChangeTimes());
			}
			return changeTimes;
		}
		
	}
}
