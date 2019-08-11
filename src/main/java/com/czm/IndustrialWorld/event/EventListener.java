package com.czm.IndustrialWorld.event;

import com.czm.IndustrialWorld.ConstItems;
import com.czm.IndustrialWorld.interfaces.MachineBlock;
import com.czm.IndustrialWorld.manager.BlockManager;
import com.czm.IndustrialWorld.utils.NBTUtil;
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
        if (NBTUtil.getTagValue(event.getItemInHand(), "isIWItem").asBoolean())
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
            if (BlockManager.hasBlock(block) &&
                BlockManager.getInstanceFromId(BlockManager.getBlockId(block)) instanceof MachineBlock) {
                BlockManager.removeBlock(block);
                block.setType(Material.AIR);
                Item item = (Item) (event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.DROPPED_ITEM));
                item.setItemStack(ConstItems.BASIC_MACHINE_BLOCK);
            }
    }
}
