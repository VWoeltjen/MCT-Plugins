package gov.nasa.arc.mct.scenario.component;

import java.util.Map;
import gov.nasa.arc.mct.scenario.util.CostType;

/**
 * Represents graph view data associated with a component.
 * Components with graph view have name, units and data for
 * drawing instantaneous cost graph or accumulative cost graph,
 * or both.
 * 
 * This should be exposed via the getCapability method of a component.
 * 
 * @author jdong2
 *
 */
public interface GraphViewCapability {
	public Map<Long, Double> getData(CostType type, boolean isInstantaneous);
	public String getUnits(CostType type, boolean isInstantaneous);
	public String getDisplayName(CostType type, boolean isInstantaneous);
	public boolean hasInstantaneous(CostType type);
	public boolean hasAccumulative(CostType type);
	public boolean hasInstantaneousGraph();
	public boolean hasAccumulativeGraph();
}
