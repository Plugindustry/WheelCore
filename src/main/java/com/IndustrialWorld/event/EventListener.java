package com.IndustrialWorld.event;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.interfaces.MachineBlock;
import com.IndustrialWorld.manager.MainManager;
import com.IndustrialWorld.utils.NBTUtil;
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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        NBTUtil.NBTValue value = NBTUtil.getTagValue(event.getItemInHand(), "isIWItem");
        if (value != null && value.asBoolean())
            MainManager.process(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (MainManager.hasBlock(event.getBlock()))
            MainManager.process(event);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks())
            if (MainManager.hasBlock(block)) {
                event.setCancelled(true);
                return;
            }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList())
            if (MainManager.hasBlock(block))
                if (MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof MachineBlock) {
                    MainManager.removeBlock(block);
                    block.setType(Material.AIR);
                    Item item = (Item) (event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.DROPPED_ITEM));
                    item.setItemStack(ConstItems.BASIC_MACHINE_BLOCK);
                } else {
                    MainManager.process(new BlockBreakEvent(block, null));
                }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            NBTUtil.NBTValue value = NBTUtil.getTagValue(item, "isIWItem");
            if (value != null && value.asBoolean())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        MainManager.process(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        MainManager.process(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        MainManager.process(event);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        MainManager.process(event);
    }
}
