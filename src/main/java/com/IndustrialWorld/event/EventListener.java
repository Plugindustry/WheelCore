package com.IndustrialWorld.event;

import com.IndustrialWorld.blocks.IWCraftingTable;
import com.IndustrialWorld.interfaces.MachineBlock;
import com.IndustrialWorld.utils.NBTUtil;
import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.manager.MainManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

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
        if(IWCraftingTable.isInvTicking(event.getInventory())){
            ItemStack[] is =  IWCraftingTable.getRecipeUsing(event.getInventory()).getMatrix();
            for (int i = 0; i <= 8; ++i){
                ItemStack i2 = event.getInventory().getStorageContents()[i + 1];
                if(i2 == null) continue;
                i2.setAmount(i2.getAmount() - is[i].getAmount());
                ItemStack[] buf = event.getInventory().getStorageContents();
                buf[i + 1] = i2;
                event.getInventory().setStorageContents(buf);
            }
        }
        for (ItemStack item : event.getInventory().getMatrix()) {
            NBTUtil.NBTValue value = NBTUtil.getTagValue(item, "isIWItem");
            if (value != null && value.asBoolean())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(IWCraftingTable.isInvTicking(event.getClickedInventory()) && event.getSlot() == 0){
            if(event.getAction() != InventoryAction.PICKUP_ALL) {
                event.setCancelled(true);
                return;
            }
            if(IWCraftingTable.getRecipeUsing(event.getClickedInventory()) == null)
                return;
            ItemStack[] is =  IWCraftingTable.getRecipeUsing(event.getClickedInventory()).getMatrix();
            for (int i = 0; i <= 8; ++i){
                ItemStack i2 = event.getClickedInventory().getStorageContents()[i + 1];
                if(i2 == null) continue;
                i2.setAmount(i2.getAmount() - is[i].getAmount());
                ItemStack[] buf = event.getClickedInventory().getStorageContents();
                buf[i + 1] = i2;
                event.getClickedInventory().setStorageContents(buf);
                ((Player) event.getClickedInventory().getViewers().get(0)).updateInventory();
            }
        }
        if(IWCraftingTable.isInvTicking(event.getInventory()))
            ((Player) event.getClickedInventory().getViewers().get(0)).updateInventory();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(IWCraftingTable.isInvTicking(event.getInventory())){
            ItemStack[] buf = event.getInventory().getStorageContents();
            buf[0] = new ItemStack(Material.AIR);
            for(ItemStack is : buf)
                if(is != null && is.getType() != Material.AIR)
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), is);
        }
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
