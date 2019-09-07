package com.IndustrialWorld.interfaces;

import org.bukkit.Material;

public abstract class OreBase extends BlockBase {

	public boolean isOre() {
		return true;
	}

	public abstract Material getMaterial();

	public abstract String getId();
}
