package com.IndustrialWorld.blocks;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.interfaces.OreBase;
import com.IndustrialWorld.manager.ItemManager;
import com.IndustrialWorld.mineral.Mineral;
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

	@Override
	public Mineral getMineral() {
		return Mineral.COPPER;
	}
}
