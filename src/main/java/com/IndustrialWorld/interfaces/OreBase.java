package com.IndustrialWorld.interfaces;

import org.bukkit.Material;

public abstract class OreBase extends BlockBase {
    @Override
    public boolean isOre() {
        return true;
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_ORE;
    }

    @Override
    public String getId() {
        return "COPPER_ORE";
    }
}
