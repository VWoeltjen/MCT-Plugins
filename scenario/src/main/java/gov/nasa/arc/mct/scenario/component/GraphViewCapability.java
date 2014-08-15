package gov.nasa.arc.mct.scenario.component;

import java.util.Collection;
import java.util.Map;

import gov.nasa.arc.mct.scenario.util.CostType;

public interface GraphViewCapability {
	public Map<Long, Double> getData(CostType type, boolean isInstantaneous);
	public String getUnits(CostType type, boolean isInstantaneous);
	public String getDisplayName(CostType type, boolean isInstantaneous);
	public boolean hasInstantaneous(CostType type);
	public boolean hasAccumulative(CostType type);
	public boolean hasInstantaneousGraph();
	public boolean hasAccumulativeGraph();
}
