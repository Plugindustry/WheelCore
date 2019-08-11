package com.IndustrialWorld.event;

import com.IndustrialWorld.interfaces.MachineBlock;
import com.IndustrialWorld.utils.NBTUtil;
import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.manager.BlockManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class EventListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        NBTUtil.NBTValue value = NBTUtil.getTagValue(event.getItemInHand(), "isIWItem");
        if (value != null && value.asBoolean())
            BlockManager.process(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (BlockManager.hasBlock(event.getBlock()))
            BlockManager.process(event);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks())
            if (BlockManager.hasBlock(block)) {
                event.setCancelled(true);
                return;
            }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList())
            if (BlockManager.hasBlock(block))
                if (BlockManager.getInstanceFromId(BlockManager.getBlockId(block)) instanceof MachineBlock) {
                    BlockManager.removeBlock(block);
                    block.setType(Material.AIR);
                    Item item = (Item) (event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.DROPPED_ITEM));
                    item.setItemStack(ConstItems.BASIC_MACHINE_BLOCK);
                } else {
                    BlockManager.process(new BlockBreakEvent(block, null));
                }
    }
}
