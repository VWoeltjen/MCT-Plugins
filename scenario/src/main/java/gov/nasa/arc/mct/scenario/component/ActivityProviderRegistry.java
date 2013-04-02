package gov.nasa.arc.mct.scenario.component;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import gov.nasa.arc.mct.scenario.spi.ActivityProvider;


public class ActivityProviderRegistry {
	
	private static Set<ActivityProvider> registry = new ConcurrentSkipListSet<ActivityProvider>(
			new Comparator<ActivityProvider>() {

				@Override
				public int compare(ActivityProvider o1, ActivityProvider o2) {
					return o1 == o2 ? 0 : System.identityHashCode(o1) - System.identityHashCode(o2); 
				}
			});
	
	/**
	 * Adds an <code>EvaluatorProvider</code> to the set of managed evaluator providers.
	 * @param EvaluatorProvider provider to use when determining evaluators.
	 */
	public void addProvider(ActivityProvider provider) {
		registry.add(provider);
	}
	
	/**
	 * Removes an <code>EvaluatorProvider</code> from the set of managed evaluator providers.
	 * @param EvaluatorProvider provider to remove from the list of active evaluators. 
	 */
	public void removeProvider(ActivityProvider provider) {
		registry.remove(provider);
	}

}
