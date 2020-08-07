package com.industrialworld.interfaces.block;

import org.bukkit.Material;

public abstract class OreBase extends DummyBlock {

    public boolean isOre() {
        return true;
    }

    public abstract Material getMaterial();

    public abstract String getId();
}
