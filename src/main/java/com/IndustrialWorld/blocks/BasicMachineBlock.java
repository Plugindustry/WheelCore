package com.IndustrialWorld.blocks;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.interfaces.BlockBase;
import org.bukkit.event.block.BlockBreakEvent;

public class BasicMachineBlock extends BlockBase {
    @Override
    public void onBreak(BlockBreakEvent event){
        event.setDropItems(false);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ConstItems.BASIC_MACHINE_BLOCK);
        super.onBreak(event);
    }
}
