package com.IndustrialWorld.blocks;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.interfaces.BlockBase;
import com.IndustrialWorld.interfaces.OreBase;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

public class CopperOre extends OreBase {
    @Override
    public void onBreak(BlockBreakEvent event){
        event.setDropItems(false);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ConstItems.COPPER_ORE);
        super.onBreak(event);
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
