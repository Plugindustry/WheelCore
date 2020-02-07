package com.IndustrialWorld.event;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.interfaces.MachineBase;
import com.IndustrialWorld.manager.InventoryListenerManager;
import com.IndustrialWorld.manager.MainManager;
import com.IndustrialWorld.manager.RecipeRegistry;
import com.IndustrialWorld.manager.recipe.RecipeBase;
import com.IndustrialWorld.manager.recipe.SmeltingRecipe;
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
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        NBTUtil.NBTValue value = NBTUtil.getTagValue(event.getItemInHand(), "isIWItem");
        if (value != null && value.asBoolean()) {
	        if (!MainManager.processBlockPlacement(event.getItemInHand(), event.getBlockPlaced())) {
	        	event.setCancelled(true);
	        }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (MainManager.hasBlock(event.getBlock())) {
        	// don't drop any item by default.
        	event.setDropItems(false);
        	if (!MainManager.processBlockDestroy(event.getPlayer().getItemInHand(), event.getBlock(), event.isCancelled())) {
        		event.setCancelled(true);
	        }
        }
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
                if (MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof MachineBase) {
                    MainManager.removeBlock(block);
                    block.setType(Material.AIR);
                    Item item = (Item) (event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.DROPPED_ITEM));
                    item.setItemStack(ConstItems.BASIC_MACHINE_BLOCK);
                } else {
                	MainManager.processBlockDestroy(null, block, false);
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
    public void onItemSmelt(FurnaceSmeltEvent event) {
        NBTUtil.NBTValue sourceValue = NBTUtil.getTagValue(event.getSource(), "isIWItem");
        if (sourceValue != null && sourceValue.asBoolean()) {
            SmeltingRecipe recipe = RecipeRegistry.matchSmeltingRecipe(event.getSource());
            if (recipe != null) {
                event.setResult(recipe.getResult());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
	    InventoryListenerManager.onInventoryClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryListenerManager.onInventoryClose(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	// item interact priority is higher than blocks
    	if (event.hasItem()) {
		    if (!MainManager.processItemInteract(event.getPlayer(), event.getClickedBlock(), event.getItem(), event.getAction())) {
			    event.setCancelled(true);
			    return;
		    }
	    }

	    if (event.hasBlock()) {
		    if (!MainManager.processBlockInteract(event.getPlayer(), event.getClickedBlock(), event.getItem(), event.getAction())) {
			    event.setCancelled(true);
		    }
	    }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        MainManager.update(event);
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) { MainManager.onWorldInit(event); }

}
