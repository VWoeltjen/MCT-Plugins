package gov.nasa.arc.mct.scenario.util;

/**
 * Aggregate naming differences of cost types.
 * The naming is mostly used in drawing cost graph.
 * @author jdong2
 *
 */
public enum CostType {
	
	COMM("COMM", "Buffered Data", "Kbps", "kb") {
		public double add(double myValue, double anotherValue) {
			return myValue + anotherValue;
		}
	},
	
	POWER("POWER", "Energy", "Watts", "Watt Hour") {
		public double add(double myValue, double anotherValue) {
			return myValue + anotherValue;
		}
	}, 
	
	/** 
	 * can be used if having constant impedance
	 * IMPEDANCE("IMPEDANCE", "Ohm", "Ohm") {
		public double add(double myValue, double anotherValue) {
			return myValue * anotherValue / (myValue + anotherValue);
		}
	}*/ ; 
	
	private String instantaneousName;
	private String accumulativeName;
	private String instantaneousUnits;
	private String accumulativeUnits;
	
	private CostType(String name, String accumulativeName, String instantaneousUnits, String accumulativeUnits) {
		this.instantaneousName = name;
		this.accumulativeName = accumulativeName;
		this.instantaneousUnits = instantaneousUnits;
		this.accumulativeUnits = accumulativeUnits;
	}

	public String getName() {
		return instantaneousName;
	}
	
	public String getAccumulativeName() {
		return accumulativeName;
	}
	
	public String getInstantaniousUnits() {
		return instantaneousUnits;
	}
	
	public String getAccumulativeUnits() {
		return accumulativeUnits;
	}
	
	public abstract double add(double myValue, double anotherValue);
	
	public static void main(String[] args) {
		for (CostType type: CostType.values()) {
			System.out.println(type.getName() + " " + type.getInstantaniousUnits() + " " + type.getAccumulativeUnits());
		}
	}
}
