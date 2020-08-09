package com.industrialworld.event;

import com.google.common.collect.Maps;
import com.industrialworld.ConstItems;
import com.industrialworld.IndustrialWorld;
import com.industrialworld.interfaces.block.MachineBase;
import com.industrialworld.manager.InventoryListenerManager;
import com.industrialworld.manager.MainManager;
import com.industrialworld.manager.RecipeRegistry;
import com.industrialworld.manager.recipe.RecipeBase;
import com.industrialworld.manager.recipe.SmeltingRecipe;
import com.industrialworld.utils.EnchantmentUtil;
import com.industrialworld.utils.ItemStackUtil;
import com.industrialworld.utils.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        NBTUtil.NBTValue value = NBTUtil.getTagValue(event.getItemInHand(), "isIWItem");
        if (value != null && value.asBoolean()) {
            event.setCancelled(!MainManager.processBlockPlacement(event.getItemInHand(), event.getBlockPlaced()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (MainManager.hasBlock(event.getBlock().getLocation())) {
            // don't drop any item by default.
            event.setDropItems(false);
            event.setCancelled(!MainManager.processBlockDestroy(event.getPlayer().getItemInHand(),
                                                                event.getBlock(),
                                                                event.isCancelled()));
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks())
            if (MainManager.hasBlock(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList())
            if (MainManager.hasBlock(block.getLocation()))
                if (MainManager.getInstanceFromId(MainManager.getBlockId(block.getLocation())) instanceof MachineBase) {
                    MainManager.removeBlock(block.getLocation());
                    block.setType(Material.AIR);
                    Item item = (Item) (event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(),
                                                                                EntityType.DROPPED_ITEM));
                    item.setItemStack(ConstItems.BASIC_MACHINE_BLOCK);
                } else {
                    MainManager.processBlockDestroy(null, block, false);
                }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack srcItem = event.getInventory().getItem(0);
        ItemStack result = event.getResult();
        if (srcItem == null || result == null)
            return;

        Map<Enchantment, Integer> src = srcItem.getEnchantments()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey() instanceof EnchantmentUtil.CustomEnchantment)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<Enchantment, Integer> customs = src.entrySet()
                .stream()
                .filter(entry -> result.getEnchantments()
                        .keySet()
                        .stream()
                        .noneMatch(enchantment -> entry.getKey().conflictsWith(enchantment)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (customs.isEmpty())
            return;

        result.addEnchantments(src);
        Maps.difference(src, customs)
                .entriesOnlyOnLeft()
                .keySet()
                .forEach(enchantment -> EnchantmentUtil.removeFromItem(result,
                                                                       (EnchantmentUtil.CustomEnchantment) enchantment));

        event.setResult(result);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory craftingInv = event.getInventory();
        if (Stream.of(craftingInv.getMatrix()).anyMatch(ItemStackUtil::isIWItem) ||
            RecipeRegistry.isPlaceholder(event.getRecipe())) {
            RecipeBase.RecipeResultInfo resultInfo = RecipeRegistry.matchCraftingRecipe(Arrays.asList(craftingInv.getMatrix()),
                                                                                        null);
            craftingInv.setResult(resultInfo == null ?
                                  null :
                                  resultInfo.getRecipe().getResult(resultInfo.getIwMaterial()));
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        CraftingInventory craftingInv = event.getInventory();
        HashMap<Integer, ItemStack> map = new HashMap<>();
        if ((Stream.of(craftingInv.getMatrix()).anyMatch(ItemStackUtil::isIWItem) ||
             RecipeRegistry.isPlaceholder(event.getRecipe())) && RecipeRegistry.matchCraftingRecipe(Arrays.asList(
                craftingInv.getMatrix()), map) != null) {
            ItemStack[] contents = craftingInv.getStorageContents();
            map.forEach((key, value) -> contents[key] = value);
            Bukkit.getScheduler().runTask(IndustrialWorld.instance, () -> craftingInv.setStorageContents(contents));
        }
    }

    @EventHandler
    public void onItemSmelt(FurnaceSmeltEvent event) {
        NBTUtil.NBTValue sourceValue = NBTUtil.getTagValue(event.getSource(), "isIWItem");
        if (sourceValue != null && sourceValue.asBoolean()) {
            SmeltingRecipe recipe = RecipeRegistry.matchSmeltingRecipe(event.getSource());
            if (recipe != null) {
                event.setResult(recipe.getResult(ItemStackUtil.getItemMaterial(event.getSource())));
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
        if (event.hasItem() && !MainManager.processItemInteract(event.getPlayer(),
                                                                event.getClickedBlock(),
                                                                event.getItem(),
                                                                event.getAction())) {
            event.setCancelled(true);
        } else if (event.hasBlock() && !MainManager.processBlockInteract(event.getPlayer(),
                                                                         event.getClickedBlock(),
                                                                         event.getItem(),
                                                                         event.getAction())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        MainManager.onWorldInit(event);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        MainManager.onChunkLoad(event);
    }

    @EventHandler
    public void onChunkUnLoad(ChunkUnloadEvent event) {
        MainManager.onChunkUnload(event);
    }
}
