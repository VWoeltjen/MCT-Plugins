package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.scenario.util.CostType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Graph data showing Power and Comm usage
 * 
 * @author jdong2
 *
 */
public class LegacyGraphData implements GraphViewCapability {

	private List<CostFunctionCapability> costs;
	
	public LegacyGraphData(AbstractComponent ac) {
		costs = ac.getCapabilities(CostFunctionCapability.class);
	}
	
	private CostFunctionCapability getCost(CostType type) {
		for (CostFunctionCapability cost: costs) {
			if (cost.getCostType().equals(type)) return cost;
		}
		return costs.get(0);
	}

	private Collection<Long> getChangeTimes(CostType type) {		
		return new TreeSet<Long>(getCost(type).getChangeTimes());
	}

	private Map<Long, Double> getInstantaneousData(CostType type) {
		Map<Long, Double> data = new TreeMap<Long, Double> ();
		for (CostFunctionCapability cost: costs) {
			if (cost.getCostType().equals(type)) {
				for (Long t : getChangeTimes(type)) {
					data.put(t, cost.getValue(t));
				}
				if (!data.containsKey(0l)) data.put(0l, 0.0);
			}			
		}		
		return data;
	}

	private Map<Long, Double> getAccumulativeData(CostType type) {
		Map<Long, Double> map = new TreeMap<Long, Double> ();
		return map;
	}

	@Override
	public Map<Long, Double> getData(CostType type, boolean isInstantaneous) {
		Map<Long, Double> data = (isInstantaneous? getInstantaneousData(type) : getAccumulativeData(type));	
		return data;
	}

	@Override
	public String getUnits(CostType type, boolean isInstantaneous) {
		return (isInstantaneous)? type.getInstantaniousUnits() : type.getAccumulativeUnits();
	}

	@Override
	public String getDisplayName(CostType type, boolean isInstantaneous) {
		return type.getName();
	}

	@Override
	public boolean hasInstantaneous(CostType type) {
		return true;
	}

	@Override
	public boolean hasAccumulative(CostType type) {
		return false;
	}

	@Override
	public boolean hasInstantaneousGraph() {
		return true;
	}

	@Override
	public boolean hasAccumulativeGraph() {
		return false;
	}
	

}
