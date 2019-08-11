package com.IndustrialWorld.blocks;

import com.IndustrialWorld.interfaces.MachineBlock;
import com.IndustrialWorld.ConstItems;
import org.bukkit.event.block.BlockBreakEvent;

public class BasicMachineBlock extends MachineBlock {
    @Override
    public void onBreak(BlockBreakEvent event){
        event.setDropItems(false);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ConstItems.BASIC_MACHINE_BLOCK);
        super.onBreak(event);
    }
}
