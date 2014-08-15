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
import gov.nasa.arc.mct.components.ObjectManager;
import gov.nasa.arc.mct.scenario.util.Battery;
import gov.nasa.arc.mct.scenario.util.CostType;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A Timeline serves as a container for activities. Multiple timelines may be arranged 
 * within a Scenario.
 * 
 *
 */
public class TimelineComponent extends CostFunctionComponent implements DurationCapability {
	private ObjectManager objectManager = new ObjectManager.ExplicitObjectManager();
	
	public String getDisplay(){
		return this.getDisplayName();
	}

	
	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (capability.isAssignableFrom(getClass())) {
			return capability.cast(this);
		} else if (capability.isAssignableFrom(objectManager.getClass())) {
			return capability.cast(objectManager);
		} else if (capability.isAssignableFrom(ScenarioCSVExportCapability.class)) {
			return capability.cast(new ScenarioCSVExportCapability(this));
		} else if (capability.isAssignableFrom(GraphViewCapability.class)) {
			return capability.cast(new TimelineGraphData());
		}
		return super.handleGetCapability(capability);
	}
	
	@Override
	public long getStart() {		
		// Time is measured as "milliseconds since start of timeline", so this is always 0
		return 0l;
	}
	@Override
	public long getEnd() {
		// The end of a Timeline is the end of its last activity
		long end = 0;
		Set<String> ignore = new HashSet<String>();
		for (AbstractComponent child : getComponents()) {
			end = Math.max(end, getEndFor(child, ignore));
		}
		return end;
	}
	
	// Recurse into collections to find possible end points
	// until base case of a component with a duration or a 
	// component with no children is reached.
	// Maintain list of things to ignore, to avoid cycles
	private long getEndFor(AbstractComponent child, Set<String> ignore) {
		ignore.add(child.getComponentId());
		DurationCapability dc = child.getCapability(DurationCapability.class);
		if (dc != null) {
			return dc.getEnd();
		} else {
			long end = 0;
			for (AbstractComponent grandchild : child.getComponents()) {
				if (!ignore.contains(grandchild.getComponentId())) {
					end = Math.max(end, getEndFor(grandchild, ignore));
				}
			}
			return end;
		}
	}
	
	@Override
	public void setStart(long start) {
		// TODO Auto-generated method stub		
	}
	@Override
	public void setEnd(long end) {
		// TODO Auto-generated method stub		
	}
	
	private class TimelineGraphData implements GraphViewCapability {
		String POWER_INSTANTANEOUS_NAME = "Current";
		String POWER_ACCUMULATIVE_NAME = "Battery Capacity";
		String POWER_INSANTANEOUS_UNITS = "A";
		String POWER_ACCUMULATIVE_UNITS = "%";
		
		public static final long SECOND_TO_MILLIS = 1000l;
		public static final long MINUTE_TO_MILLIS = 60000l;
		public static final long HOUR_TO_MILLIS = 3600000l;
		private final long TIME_SCALE = Battery.TIME * MINUTE_TO_MILLIS;
		
		private List<CostFunctionCapability> costs;
		
		// use TreeMap to enable ordering of data
		private Map<Long, Double> currentMap = new TreeMap<Long, Double> ();
		private Map<Long, Double> capacityMap = new TreeMap<Long, Double> ();
		
		public TimelineGraphData() {
			costs = getCapabilities(CostFunctionCapability.class);
		}
		
		private CostFunctionCapability getCost(CostType type) {
			for (CostFunctionCapability cost: costs) {
				if (cost.getCostType().equals(type)) return cost;
			}
			return costs.get(0);
		}

		private Collection<Long> getChangeTimes(CostType type) {
			Collection<Long> changeTimes = new TreeSet<Long>(getCost(type).getChangeTimes());
			if (type.equals(CostType.POWER)) { // decrease time interval according to battery value precision (currently 5 mins)
				int size = changeTimes.size();
				long start, end = 0;
				Long[] timeType = new Long[] {};
				Long[] timeArray = changeTimes.toArray(timeType);
				for (int i = 0; i < size - 1; i++ ) {
					start = timeArray[i];
					end = timeArray[i + 1];
					for (int j = 0; j < (end - start) / TIME_SCALE; j++) {
						changeTimes.add(start + j * TIME_SCALE);
					}
				}
				changeTimes.add(end);
			}
			return changeTimes;
		}

		@Override
		public Map<Long, Double> getData(CostType type, boolean isInstantaneous) {
			if (type.equals(CostType.POWER)) return getPowerData(getCost(type), isInstantaneous);
			else if (type.equals(CostType.COMM)) return getCommData(getCost(type), isInstantaneous);
			return null;
		}
		
		private void initCurrentAndCapacity(CostFunctionCapability powerCost) {
			Battery battery = new Battery();
			double capacity = battery.getCapacity();
		    double voltage, current, power;
		    // double previousCapacity = capacity;
		    // double previousCurrent = current;

		    Collection<Long> changeTimes = getChangeTimes(CostType.POWER);
				
			for (Long t: changeTimes) {		
				capacityMap.put(t, capacity);
				power = powerCost.getValue(t);
				if ((battery.getCapacity() < 100.0) || ((battery.getCapacity() == 100.0) && (power > 0.0))) {
					voltage = battery.getVoltage();	
					current = power / voltage;
					capacity = battery.setCapacity(power, 0.0); // use 5 mins as interval
					// capacity = battery.setCapacity(power, (time[i + 1] - t) / HOUR_TO_MILLIS * 1.0); 	
				} else {
					capacity = 100.0;
					current = 0.0;
				}				
				currentMap.put(t, current);
			}				
		}
		
		private Map<Long, Double> getPowerData(CostFunctionCapability powerCost, boolean isInstantaneous) {
			if (capacityMap.isEmpty()) initCurrentAndCapacity(powerCost);
			return isInstantaneous? currentMap : capacityMap;
		}
		
		private Map<Long, Double> getCommData(CostFunctionCapability commCost, boolean isInstantaneous) {
			Collection<Long> changeTimes = getChangeTimes(CostType.COMM);
			Map<Long, Double> data = new TreeMap<Long, Double> ();
			if (isInstantaneous) {
				for (Long t : changeTimes) {
					data.put(t, commCost.getValue(t));
				}
			} else {
				Long[] timeType = new Long[] {};
				Long[] time = changeTimes.toArray(timeType);
				int size = changeTimes.size();
				double currentValue = 0;
				for (int i = 0; i < size; i++) {
					long t = time[i];
					data.put(t, currentValue);
					double commValue = commCost.getValue(t);
					double increase = ((i < size - 1) ? commValue * (time[i + 1] - t) / SECOND_TO_MILLIS : 0.0);
					currentValue += increase;	
				}
			}
			return data;
		}

		@Override
		public String getUnits(CostType type, boolean isInstantaneous) {
			if (type.equals(CostType.POWER)) {
				return isInstantaneous? POWER_INSANTANEOUS_UNITS : POWER_ACCUMULATIVE_UNITS;
			} else if (type.equals(CostType.COMM)) {
				return isInstantaneous? type.getInstantaniousUnits() : type.getAccumulativeUnits();
			}
			return "";
		}

		@Override
		public String getDisplayName(CostType type, boolean isInstantaneous) {
			if (type.equals(CostType.POWER)) {
				return isInstantaneous? POWER_INSTANTANEOUS_NAME : POWER_ACCUMULATIVE_NAME;
			} else if (type.equals(CostType.COMM)) {
				return isInstantaneous? type.getName() : type.getAccumulativeName();
			}
			return "";
		}

		@Override
		public boolean hasInstantaneous(CostType type) {
			return true;
		}

		@Override
		public boolean hasAccumulative(CostType type) {
			return true;
		}

		@Override
		public boolean hasInstantaneousGraph() {
			return true;
		}

		@Override
		public boolean hasAccumulativeGraph() {
			return true;
		}
		
	}
}