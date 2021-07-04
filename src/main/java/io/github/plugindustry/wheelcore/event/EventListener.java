package io.github.plugindustry.wheelcore.event;

import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.block.Destroyable;
import io.github.plugindustry.wheelcore.interfaces.inventory.InventoryClickInfo;
import io.github.plugindustry.wheelcore.inventory.ClassicInventoryInteractor;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.manager.RecipeRegistry;
import io.github.plugindustry.wheelcore.manager.recipe.RecipeBase;
import io.github.plugindustry.wheelcore.manager.recipe.SmeltingRecipe;
import io.github.plugindustry.wheelcore.utils.EnchantmentUtil;
import io.github.plugindustry.wheelcore.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (ItemStackUtil.isPIItem(event.getItemInHand())) {
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
        if (Stream.of(craftingInv.getMatrix()).anyMatch(ItemStackUtil::isPIItem) ||
            RecipeRegistry.isPlaceholder(event.getRecipe())) {
            RecipeBase result = RecipeRegistry.matchCraftingRecipe(Arrays.asList(craftingInv.getMatrix()), null);
            craftingInv.setResult(result == null ? null : result.getResult());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        CraftingInventory craftingInv = event.getInventory();
        HashMap<Integer, ItemStack> map = new HashMap<>();
        if ((Stream.of(craftingInv.getMatrix()).anyMatch(ItemStackUtil::isPIItem) ||
             RecipeRegistry.isPlaceholder(event.getRecipe())) && RecipeRegistry.matchCraftingRecipe(Arrays.asList(
                craftingInv.getMatrix()), map) != null) {
            ItemStack[] contents = craftingInv.getStorageContents();
            map.forEach((key, value) -> contents[key] = value);
            Bukkit.getScheduler().runTask(WheelCore.instance, () -> craftingInv.setStorageContents(contents));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemSmelt(FurnaceSmeltEvent event) {
        if (ItemStackUtil.isPIItem(event.getSource())) {
            SmeltingRecipe recipe = RecipeRegistry.matchSmeltingRecipe(event.getSource());
            if (recipe != null) {
                event.setResult(recipe.getResult());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // item interact priority is higher than blocks
        if (event.hasItem() &&
            event.useItemInHand() != Event.Result.DENY &&
            !MainManager.processItemInteract(event.getPlayer(),
                                             event.getClickedBlock(),
                                             event.getItem(),
                                             event.getAction())) {
            event.setUseItemInHand(Event.Result.DENY);
        } else if (event.hasBlock() &&
                   event.useInteractedBlock() != Event.Result.DENY &&
                   !MainManager.processBlockInteract(event.getPlayer(),
                                                     Objects.requireNonNull(event.getClickedBlock()),
                                                     event.getItem(),
                                                     event.getAction())) {
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inv = event.getClickedInventory();
        if (inv != null && inv.getHolder() instanceof ClassicInventoryInteractor)
            // Detected InventoryWindow click event
            event.setCancelled(((ClassicInventoryInteractor) inv.getHolder()).processClick(new InventoryClickInfo(event)));
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent event) {
        if (event.getCommand().equals("reload")) {
            event.setCancelled(true);
            WheelCore.instance.getLogger().log(Level.WARNING,
                                               "With WheelCore installed, you cannot perform this operation which will cause severe problems. Restart your server to reload plugins instead.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void prePlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equals("/reload")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.DARK_RED +
                                          "With WheelCore installed, you cannot perform this operation which will cause severe problems. Restart your server to reload plugins instead.");
        }
    }
}
