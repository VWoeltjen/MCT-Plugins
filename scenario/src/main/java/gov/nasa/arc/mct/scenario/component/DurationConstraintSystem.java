package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DurationConstraintSystem {
	private Map<DurationCapability, List<DurationConstraint>> constraints =
			new HashMap<DurationCapability, List<DurationConstraint>>();
	private Map<DurationCapability, DurationEdge[]> edges =
			new HashMap<DurationCapability, DurationEdge[]>();
	
	public DurationConstraintSystem ( AbstractComponent root ) {
		addConstraintsFor(root, new HashSet<String>());
	}
	
	private void addConstraintsFor(AbstractComponent parent, Set<String> ignore) {
		ignore.add(parent.getComponentId());
		DurationCapability pdc = parent.getCapability(DurationCapability.class);
		if (pdc != null) {
			List<AbstractComponent> children = getChildren(parent, ignore);
			
			// Add parent/child constraints
			if (children.size() > 0) {
				for (int sign : new int[]{-1, 1}) {
					DurationCapability cdc = 
							children.get(sign < 0 ? 0 : (children.size()-1))
							.getCapability(DurationCapability.class);
					if (cdc != null) {
						// Parent edge pushes child's same edge
						addConstraint(pdc, sign, cdc, sign, false, false);
						// Child edge pushes parent's same edge & causes expansion
						addConstraint(cdc, sign, pdc, sign, false, true);
					}
				}
			}			
			
			// Add peer constraints
			for (int i = 0; i < children.size()-1; i++) {
				AbstractComponent a = children.get(i);
				AbstractComponent b = children.get(i+1);
				DurationCapability adc = a.getCapability(DurationCapability.class);
				DurationCapability bdc = b.getCapability(DurationCapability.class);		
				if (adc != null && bdc != null) {
					boolean pulls = a instanceof DecisionComponent || b instanceof DecisionComponent;
					addConstraint(adc, 1, bdc, -1, pulls, false);
					addConstraint(bdc, -1, adc, 1, pulls, false);
				}
			}
			
			// Recursively build remaining constraint hierarchy
			for (AbstractComponent child : children) {
				addConstraintsFor(child, ignore);
			}
		}		
	}
	
	private List<AbstractComponent> getChildren(AbstractComponent parent, Set<String> ignore) {
		List<AbstractComponent> children = new ArrayList<AbstractComponent>();
		
		// Assemble all children who offer a durationcapability
		for (AbstractComponent child : parent.getComponents()) {
			if (!ignore.contains(child.getComponentId())) {
				if (child.getCapability(DurationCapability.class) != null){
					children.add(child);
					ignore.add(child.getComponentId());
				}
			}
		}
		
		// Sort by time; note, this assumes all children still have DurationCapability
		Collections.sort(children, new Comparator<AbstractComponent>() {
			@Override
			public int compare(AbstractComponent a, AbstractComponent b) {				
				return Long.valueOf(
							a.getCapability(DurationCapability.class).getStart()
						).compareTo(
							b.getCapability(DurationCapability.class).getStart()
						);
			}			
		});
		
		return children;
	}
	
	public void changeAll(AbstractComponent root) {
		changeAll (root, new HashSet<String>());
	}
	
	private void changeAll(AbstractComponent root, Set<String> ignore) {
		if (!ignore.contains(root.getComponentId())) {
			ignore.add(root.getComponentId());

			for (AbstractComponent child : root.getComponents()) {
				changeAll(child, ignore);
			}
			
			DurationCapability dc = root.getCapability(DurationCapability.class);
			if (dc != null) {
				change(dc, -1);
				change(dc, 1);
			}
			
		}
	}
	
	public void change(DurationCapability dc, int sign) {
		List<DurationConstraint> constraints = this.constraints.get(dc);
		if (constraints != null) {
			int sz = constraints.size();
			for (int i = 0; i < sz; i++) {
				constraints.get(i).change(sign);
			}
		}
	}	
	
	public void addConstraint(
			DurationCapability source, int sourceSign, 
			DurationCapability target, int targetSign,
			boolean pulls, boolean expands) {
		addDurationCapability(source);
		constraints.get(source).add(
				new DurationConstraint(
						getEdge(source, sourceSign),
						getEdge(target, targetSign),
						pulls, expands));
	}
	
	private DurationEdge getEdge(DurationCapability dc, int sign) {
		addDurationCapability(dc);
		return edges.get(dc)[sign < 0 ? 0 : 1];
	}
	
	private void addDurationCapability(DurationCapability dc) {
		if (!edges.containsKey(dc)) {
			edges.put(dc, new DurationEdge[] {
				new DurationEdge(dc, -1), new DurationEdge(dc, 1)	
			});
		}
		if (!constraints.containsKey(dc)) {
			constraints.put(dc, new ArrayList<DurationConstraint>());
		}
	}
	
	
	private class DurationConstraint {
		private DurationEdge source;
		private DurationEdge target;
		private boolean pulls;
		private boolean expands;
		
		public DurationConstraint(DurationEdge source, DurationEdge target,
				boolean pulls, boolean expands) {
			super();
			this.source = source;
			this.target = target;
			this.pulls = pulls;
			this.expands = expands;
		}
		
		public void change(int sign) {
			int cmp = target.compare(source);
			cmp *= expands ? -1 : 1;
			boolean violates = pulls ? (cmp != 0) : (cmp < 0);
			if (violates) {
				long newValue = source.get();
				long diff = source.get() - target.get();
				if (!expands) { // Change both edges
					target.set(-1, target.get(-1) + diff);
				}							
				target.set(newValue);
				DurationConstraintSystem.this.change(target.dc, (int)(Math.signum(diff)));
			}
		}		
	}
	
	private class DurationEdge {
		private DurationCapability dc;
		private int sign;
		
		public DurationEdge(DurationCapability dc, int sign) {
			super();
			this.dc = dc;
			this.sign = sign;
		}
		
		public long get() {
			return get(1);
		}
		
		public long get(int s) {
			return sign * s< 0 ? dc.getStart() : dc.getEnd();
		}
		
		public void set(long value) {
			set(1, value);
		}
		
		public void set(int s, long value) {
			if (sign * s < 0) {
				dc.setStart(value);
			} else {
				dc.setEnd(value);
			}
		}
		
		/**
		 * Compares a value to a given edge, taking into account 
		 * the edge's direction.
		 * 
		 * @param value the timestamp to compare
		 * @return a negative integer if bounded by edge, zero if equal
		 */
		public int compare(DurationEdge other) {
			return sign * Long.valueOf(other.get()).compareTo(get());
		}
	}
}
