package com.IndustrialWorld.blocks;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.interfaces.OreBase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CopperOre extends OreBase {
	@Override
	public ItemStack getItemStack() {
		return ConstItems.COPPER_ORE;
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
