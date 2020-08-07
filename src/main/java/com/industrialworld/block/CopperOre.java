package com.industrialworld.block;

import com.industrialworld.ConstItems;
import com.industrialworld.interfaces.block.OreBase;
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
