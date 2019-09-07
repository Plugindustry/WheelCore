package com.IndustrialWorld.blocks;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.interfaces.BlockBase;
import com.IndustrialWorld.interfaces.OreBase;
import org.bukkit.event.block.BlockBreakEvent;

public class CopperOre extends OreBase {
    @Override
    public void onBreak(BlockBreakEvent event){
        event.setDropItems(false);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ConstItems.COPPER_ORE);
        super.onBreak(event);
    }
}
