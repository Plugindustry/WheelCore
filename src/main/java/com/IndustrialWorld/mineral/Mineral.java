package com.IndustrialWorld.mineral;

public enum Mineral {
	COPPER("copper", "Cu", 0.9),
	IRON("iron", "Fe", 0.8),
	CALCIUM("calcium", "Ca", 0.83),
	POTASSIUM("potassium", "K", 0.85),
	SLIVER("sliver", "Ag", 0.95),
	TIN("tin", "Sn", 0.78),
	LEAD("lead", "Pb", 0.75),
	SODIUM("sodium", "Na", 0.8),
	ALUMINIUM("aluminium", "Al", 0.85),
	GOLD("gold", "Au", 0.87);
	private String mineralName;

	// Easy to identify because our native language is not English :P
	// Maybe useful soon, so don't delete it right now.
	private String chemicalName;
	// The bigger, the lesser power loss.
	// Values might be changed soon.
	private double conductivity;

	Mineral(String mineralName, String chemicalName, double conductivity) {
		this.mineralName = mineralName;
		this.chemicalName = chemicalName;
		this.conductivity = conductivity;
	}

	public String getMineralName() {
		return mineralName;
	}

	public String getChemicalName() {
		return chemicalName;
	}

	public double getConductivity() {
		return conductivity;
	}
}
