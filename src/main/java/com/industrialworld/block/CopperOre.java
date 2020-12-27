package com.industrialworld.block;

import com.industrialworld.ConstItems;
import com.industrialworld.interfaces.block.DummyBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CopperOre extends DummyBlock {
    @Override
    public ItemStack getItemStack() {
        return ConstItems.COPPER_ORE;
    }

    @Override
    public Material getMaterial() {
        return ConstItems.COPPER_ORE.getType();
    }

}
