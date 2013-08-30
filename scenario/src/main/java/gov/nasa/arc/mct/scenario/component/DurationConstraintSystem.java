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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DurationConstraintSystem {
	private Map<DurationCapability, AbstractComponent> components =
			new HashMap<DurationCapability, AbstractComponent>();
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
					AbstractComponent child = 
							children.get(sign < 0 ? 0 : (children.size()-1));
					DurationCapability cdc = 
							child.getCapability(DurationCapability.class);
					if (cdc != null) {
						// Permit reverse-lookup of component later
						components.put(cdc, child);
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
					// Permit reverse-lookup of component later
					components.put(adc, a);
					components.put(bdc, b);
					// Construct appropriate peer constraints (push or pull)
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
	
	public Set<AbstractComponent> changeAll(AbstractComponent root) {
		Set<AbstractComponent> changed = new HashSet<AbstractComponent>();
		changeAll (root, new HashSet<String>(), changed);
		return changed;
	}
	
	private void changeAll(AbstractComponent root, Set<String> ignore, Set<AbstractComponent> changed) {
		if (!ignore.contains(root.getComponentId())) {
			ignore.add(root.getComponentId());

			for (AbstractComponent child : root.getComponents()) {
				changeAll(child, ignore, changed);
			}
			
			DurationCapability dc = root.getCapability(DurationCapability.class);
			if (dc != null) {
				change(dc, -1, changed);
				change(dc, 1, changed);
			}
			
		}
	}
	
	public Set<AbstractComponent> change(DurationCapability dc, int sign) {
		Set<AbstractComponent> changed = new HashSet<AbstractComponent>();
		change(dc, sign, changed);
		return changed;
	}	
	
	private void change(DurationCapability dc, int sign, Set<AbstractComponent> changed) {
		List<DurationConstraint> constraints = this.constraints.get(dc);
		if (constraints != null) {
			int sz = constraints.size();
			for (int i = 0; i < sz; i++) {
				constraints.get(i).change(sign, changed);
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
		
		public void change(int sign, Set<AbstractComponent> changed) {
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
				// Track changes
				AbstractComponent comp = components.get(target.dc);
				if (comp != null) {
					changed.add(components.get(target.dc));
				}
				DurationConstraintSystem.this.change(target.dc, (int)(Math.signum(diff)), changed);
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
