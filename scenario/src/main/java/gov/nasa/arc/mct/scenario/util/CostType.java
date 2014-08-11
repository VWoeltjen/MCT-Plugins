package gov.nasa.arc.mct.scenario.util;

public enum CostType {
	
	COMM("COMM", "Kbps", "kb") {
		public double add(double myValue, double anotherValue) {
			return myValue + anotherValue;
		}
	},
	
	POWER("POWER", "Watts", "Watt Hour") {
		public double add(double myValue, double anotherValue) {
			return myValue + anotherValue;
		}
	}, 
	
	/** IMPEDANCE("IMPEDANCE", "Ohm", "Ohm") {
		public double add(double myValue, double anotherValue) {
			return myValue * anotherValue / (myValue + anotherValue);
		}
	}*/ ; 
	
	private String name;
	private String instantaniousUnits;
	private String accumulativeUnits;
	
	private CostType(String name, String instantaniousUnits, String accumulativeUnits) {
		this.name = name;
		this.instantaniousUnits = instantaniousUnits;
		this.accumulativeUnits = accumulativeUnits;
	}

	public String getName() {
		return name;
	}
	
	public String getInstantaniousUnits() {
		return instantaniousUnits;
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
