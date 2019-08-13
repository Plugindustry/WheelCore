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
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Damageable;

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
        for (ItemStack item : event.getInventory().getMatrix()) {
            NBTUtil.NBTValue value = NBTUtil.getTagValue(item, "isIWItem");
            if (value != null && value.asBoolean())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (IWCraftingTable.isInvTicking(event.getClickedInventory()) && event.getSlot() == 0) {
            if (event.getAction() != InventoryAction.PICKUP_ALL) {
                event.setCancelled(true);
                return;
            }
            Recipe recipe = IWCraftingTable.getRecipeUsing(event.getClickedInventory());
            if (recipe == null)
                return;
            if (recipe instanceof IWCraftingTable.IWShapedRecipe) {
                IWCraftingTable.IWShapedRecipe rb = (IWCraftingTable.IWShapedRecipe) recipe;
                ItemStack[] is = rb.getMatrix();
                for (int i = 0; i <= 8; ++i) {
                    ItemStack i2 = event.getClickedInventory().getStorageContents()[i + 1];
                    if (i2 == null || is[i] == null)
                        continue;
                    i2.setAmount(i2.getAmount() - is[i].getAmount());
                    ItemStack[] buf = event.getClickedInventory().getStorageContents();
                    buf[i + 1] = i2;
                    event.getClickedInventory().setStorageContents(buf);
                }
            } else if (recipe instanceof IWCraftingTable.IWShapelessRecipe) {
                Inventory ci = event.getClickedInventory();
                IWCraftingTable.IWShapelessRecipe rb = (IWCraftingTable.IWShapelessRecipe) recipe;
                List<ItemStack> i1 = Arrays.asList(ci.getStorageContents()).subList(1, ci.getStorageContents().length);
                ItemStack[] i2 = rb.getMatrix();
                for (int i = 0; i <= 8; ++i) {
                    ItemStack bi = i1.get(i);
                    if (bi == null || i2[i] == null)
                        continue;
                    if (rb.isUseDurability() && i2[i].getType().getMaxDurability() != 0) {
                        bi.setDurability((short)(bi.getDurability() + rb.getDurabilityCost()));
                    } else {
                        bi.setAmount(bi.getAmount() - i2[i].getAmount());
                    }
                    ItemStack[] buf = event.getClickedInventory().getStorageContents();
                    buf[i + 1] = bi;
                    event.getClickedInventory().setStorageContents(buf);
                }
            }
        }
        /*if (IWCraftingTable.isInvTicking(event.getClickedInventory()) && event.getSlot() == 0)
            ((Player) event.getClickedInventory().getViewers().get(0)).updateInventory();*/
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (IWCraftingTable.isInvTicking(event.getInventory())) {
            ItemStack[] buf = event.getInventory().getStorageContents();
            buf[0] = new ItemStack(Material.AIR);
            for (ItemStack is : buf)
                if (is != null && is.getType() != Material.AIR)
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
