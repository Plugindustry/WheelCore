package com.czm.IndustrialWorld.blocks;

import com.czm.IndustrialWorld.ConstItems;
import com.czm.IndustrialWorld.interfaces.MachineBlock;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;

public class BasicMachineBlock extends MachineBlock {
    @Override
    public void onBreak(BlockBreakEvent event){
        event.setDropItems(false);
        Item item = (Item) (event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.DROPPED_ITEM));
        item.setItemStack(ConstItems.BASIC_MACHINE_BLOCK);
        super.onBreak(event);
    }
}
