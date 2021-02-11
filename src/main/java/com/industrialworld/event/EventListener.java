package com.industrialworld.event;

import com.industrialworld.IndustrialWorld;
import com.industrialworld.interfaces.block.Destroyable;
import com.industrialworld.inventory.WindowInteractor;
import com.industrialworld.manager.MainManager;
import com.industrialworld.manager.RecipeRegistry;
import com.industrialworld.manager.recipe.RecipeBase;
import com.industrialworld.manager.recipe.SmeltingRecipe;
import com.industrialworld.utils.EnchantmentUtil;
import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (ItemStackUtil.isIWItem(event.getItemInHand())) {
            event.setCancelled(!MainManager.processBlockPlacement(event.getItemInHand(), event.getBlockPlaced()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (MainManager.hasBlock(event.getBlock().getLocation())) {
            // don't drop any item by default.
            event.setDropItems(false);
            event.setCancelled(!MainManager.processBlockDestroy(event.getPlayer().getInventory().getItemInMainHand(),
                                                                event.getBlock(),
                                                                Destroyable.DestroyMethod.PLAYER_DESTROY));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks())
            if (MainManager.hasBlock(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Iterator<Block> iterator = event.blockList().iterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            if (MainManager.hasBlock(block.getLocation())) {
                iterator.remove();
                if (MainManager.processBlockDestroy(null, block, Destroyable.DestroyMethod.EXPLOSION))
                    block.setType(Material.AIR);
            }
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

        Map<Enchantment, Integer> conflicts = src.entrySet()
                .stream()
                .filter(entry -> result.getEnchantments()
                        .keySet()
                        .stream()
                        .anyMatch(enchantment -> entry.getKey().conflictsWith(enchantment)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (conflicts.size() == src.size())
            return;

        result.addUnsafeEnchantments(src);
        conflicts.forEach((enchantment, level) -> EnchantmentUtil.removeFromItem(result,
                                                                                 (EnchantmentUtil.CustomEnchantment) enchantment,
                                                                                 level));

        event.setResult(result);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory craftingInv = event.getInventory();
        if (Stream.of(craftingInv.getMatrix()).anyMatch(ItemStackUtil::isIWItem) ||
            RecipeRegistry.isPlaceholder(event.getRecipe())) {
            RecipeBase result = RecipeRegistry.matchCraftingRecipe(Arrays.asList(craftingInv.getMatrix()), null);
            craftingInv.setResult(result == null ? null : result.getResult());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemSmelt(FurnaceSmeltEvent event) {
        if (ItemStackUtil.isIWItem(event.getSource())) {
            SmeltingRecipe recipe = RecipeRegistry.matchSmeltingRecipe(event.getSource());
            if (recipe != null) {
                event.setResult(recipe.getResult());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // item interact priority is higher than blocks
        if (event.hasItem() && !MainManager.processItemInteract(event.getPlayer(),
                                                                event.getClickedBlock(),
                                                                event.getItem(),
                                                                event.getAction())) {
            event.setCancelled(true);
        } else if (event.hasBlock() && !MainManager.processBlockInteract(event.getPlayer(),
                                                                         Objects.requireNonNull(event.getClickedBlock()),
                                                                         event.getItem(),
                                                                         event.getAction())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return ;
        }

        Player p = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof WindowInteractor) {
            // Detected InventoryWindow click event
            WindowInteractor interactor = (WindowInteractor) inv.getHolder();
            if (interactor.processClick(event.getSlot(), event)) {
                event.setCancelled(true);
            }
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
