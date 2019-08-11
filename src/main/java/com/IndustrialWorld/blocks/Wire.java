package com.IndustrialWorld.blocks;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.interfaces.BlockBase;
import org.bukkit.event.block.BlockBreakEvent;

public class Wire extends BlockBase {
    @Override
    public void onBreak(BlockBreakEvent event){
        event.setDropItems(false);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ConstItems.IW_WIRE);
        super.onBreak(event);
    }
}