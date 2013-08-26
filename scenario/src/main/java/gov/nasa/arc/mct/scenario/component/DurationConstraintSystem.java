package gov.nasa.arc.mct.scenario.component;

import java.util.Map;

public class DurationConstraintSystem {
	private Map<DurationCapability, DurationConstraint[]> constraints;
	private Map<DurationCapability, DurationEdge[]> edges;
	
	public void change(DurationCapability dc, int sign) {
		DurationConstraint[] constraints = this.constraints.get(dc);
		if (constraints != null) {
			for (int i = 0; i < 2; i++) {
				constraints[sign < 0 ? i : (1-i)].change(sign);
			}
		}
	}	
	
	public void addConstraint(
			DurationCapability source, int sourceSign, 
			DurationCapability target, int targetSign,
			boolean pulls, boolean expands) {
		addDurationCapability(source);
		constraints.get(source)[ sourceSign < 0 ? 0 : 1 ] =
				new DurationConstraintImpl(
						getEdge(source, sourceSign),
						getEdge(target, targetSign),
						pulls, expands);
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
			constraints.put(dc, new DurationConstraint[] {
					NULL_CONSTRAINT, NULL_CONSTRAINT
			});
		}
	}
	
	private interface DurationConstraint {
		public void change(int sign);
	}
	
	private static final DurationConstraint NULL_CONSTRAINT = new DurationConstraint() {
		@Override
		public void change(int sign) {}		
	};
	
	private class DurationConstraintImpl implements DurationConstraint {
		private DurationEdge source;
		private DurationEdge target;
		private boolean pulls;
		private boolean expands;
		
		public DurationConstraintImpl(DurationEdge source, DurationEdge target,
				boolean pulls, boolean expands) {
			super();
			this.source = source;
			this.target = target;
			this.pulls = pulls;
			this.expands = expands;
		}
		
		public void change(int sign) {
			int cmp = target.compare(source);
			boolean violates = pulls ? (cmp <= 0) : (cmp < 0);
			if (violates) {
				long newValue = source.get();
				if (!expands) { // Change both edges
					long diff = source.get() - target.get();
					target.set(-1, target.get(-1) + diff);
				}							
				target.set(newValue);
				DurationConstraintSystem.this.change(target.dc, sign);
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
