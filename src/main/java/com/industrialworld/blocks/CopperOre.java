package com.industrialworld.blocks;

import com.industrialworld.ConstItems;
import com.industrialworld.interfaces.OreBase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CopperOre extends OreBase {
    @Override
    public ItemStack getItemStack() {
        return ConstItems.COPPER_ORE;
    }

    @Override
    public Material getMaterial() {
        return ConstItems.COPPER_ORE.getType();
    }

    @Override
    public String getId() {
        return "COPPER_ORE";
    }
}
