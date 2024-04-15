package io.github.plugindustry.wheelcore.event;

import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.Interactive;
import io.github.plugindustry.wheelcore.interfaces.block.*;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityBase;
import io.github.plugindustry.wheelcore.interfaces.item.Damageable;
import io.github.plugindustry.wheelcore.interfaces.item.Placeable;
import io.github.plugindustry.wheelcore.interfaces.item.*;
import io.github.plugindustry.wheelcore.internal.shadow.CraftHumanEntity;
import io.github.plugindustry.wheelcore.internal.shadow.DamageSource;
import io.github.plugindustry.wheelcore.internal.shadow.PlayerInventory;
import io.github.plugindustry.wheelcore.manager.EntityDamageHandler;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.manager.RecipeRegistry;
import io.github.plugindustry.wheelcore.manager.recipe.RecipeBase;
import io.github.plugindustry.wheelcore.manager.recipe.SmeltingRecipe;
import io.github.plugindustry.wheelcore.utils.EnchantmentUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventListener implements Listener {
    // methods return true if the event doesn't need to be cancelled

    private static List<ItemStack> fillMatrix(ItemStack[] matrix) {
        if (matrix.length == 9) return Arrays.asList(matrix);
        else if (matrix.length == 4)
            return Arrays.asList(matrix[0], matrix[1], null, matrix[2], matrix[3], null, null, null, null);
        else throw new IllegalArgumentException("Illegal matrix size");
    }

    private static Comparator<Block> getSuitableComparator(BlockFace direction) {
        if (direction.getModX() == 1) return Comparator.comparingDouble(Block::getX).reversed();
        else if (direction.getModX() == -1) return Comparator.comparingDouble(Block::getX);
        else if (direction.getModY() == 1) return Comparator.comparingDouble(Block::getY).reversed();
        else if (direction.getModY() == -1) return Comparator.comparingDouble(Block::getY);
        else if (direction.getModZ() == 1) return Comparator.comparingDouble(Block::getZ).reversed();
        else if (direction.getModZ() == -1) return Comparator.comparingDouble(Block::getZ);
        else throw new IllegalArgumentException("Invalid direction");
    }

    private static void pistonMove(BlockFace direction, List<Block> blocks) {
        blocks.stream().filter(block -> MainManager.hasBlock(block.getLocation()))
                .sorted(getSuitableComparator(direction)).forEachOrdered(block -> {
                    Location loc = block.getLocation();
                    BlockBase instance = MainManager.getBlockInstance(loc);
                    Map<NamespacedKey, BlockData> data = new HashMap<>(MainManager.blockDataProvider.allDataAt(loc));
                    MainManager.removeBlock(loc);
                    MainManager.blockDataProvider.addBlock(block.getRelative(direction).getLocation(),
                            Objects.requireNonNull(instance), data);
                });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemBase instance = MainManager.getItemInstance(event.getItemInHand());
        if (instance != null) event.setCancelled(!(instance instanceof Placeable &&
                                                   ((Placeable) instance).onItemPlace(event.getItemInHand(),
                                                           event.getBlockPlaced(), event.getBlockAgainst(),
                                                           event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack toolItem = event.getPlayer().getInventory().getItemInMainHand();
        ItemBase itemInstance = MainManager.getItemInstance(toolItem);
        Block block = event.getBlock();
        if (itemInstance instanceof Tool) {
            try {
                ((Tool) itemInstance).getOverrideItemDrop(block, toolItem).ifPresent(items -> {
                    event.setDropItems(false);
                    items.forEach(item -> block.getWorld().dropItemNaturally(block.getLocation(), item));
                });
                ((Tool) itemInstance).getOverrideExpDrop(block, toolItem).ifPresent(event::setExpToDrop);
            } catch (Throwable t) {
                WheelCore.getInstance().getLogger()
                        .log(Level.SEVERE, t, () -> "Error while overriding block break behavior");
            }
        }

        if (MainManager.hasBlock(event.getBlock().getLocation())) {
            // don't drop any item by default.
            event.setDropItems(false);
            Block target = event.getBlock();
            BlockBase blockBase = MainManager.getBlockInstance(target.getLocation());
            event.setCancelled(!(blockBase instanceof Destroyable && ((Destroyable) blockBase).onBlockDestroy(target,
                    Destroyable.DestroyMethod.PLAYER_DESTROY, event.getPlayer().getInventory().getItemInMainHand(),
                    event.getPlayer())));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Block piston = event.getBlock();
        BlockFace direction = event.getDirection();
        List<Block> blocks = event.getBlocks();
        if (!blocks.stream().filter(block -> MainManager.hasBlock(block.getLocation())).allMatch(block -> {
            BlockBase instance = MainManager.getBlockInstance(block.getLocation());
            try {
                return instance instanceof PistonPushable &&
                       ((PistonPushable) instance).onPistonPush(block, piston, direction, blocks);
            } catch (Throwable t) {
                WheelCore.getInstance().getLogger()
                        .log(Level.SEVERE, t, () -> "Error while processing piston extend event");
                return false;
            }
        })) {
            event.setCancelled(true);
            return;
        }

        pistonMove(direction, blocks);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        Block piston = event.getBlock();
        BlockFace direction = event.getDirection();
        List<Block> blocks = event.getBlocks();
        if (!blocks.stream().filter(block -> MainManager.hasBlock(block.getLocation())).allMatch(block -> {
            BlockBase instance = MainManager.getBlockInstance(block.getLocation());
            try {
                return instance instanceof PistonPullable &&
                       ((PistonPullable) instance).onPistonPull(block, piston, direction, blocks);
            } catch (Throwable t) {
                WheelCore.getInstance().getLogger()
                        .log(Level.SEVERE, t, () -> "Error while processing piston retract event");
                return false;
            }
        })) {
            event.setCancelled(true);
            return;
        }

        pistonMove(direction, blocks);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (MainManager.hasBlock(block.getLocation())) {
            BlockBase instance = MainManager.getBlockInstance(block.getLocation());
            event.setCancelled(true);
            if (event.getChangedType() == Material.AIR && !(instance instanceof Destroyable &&
                                                            ((Destroyable) instance).onBlockDestroy(block,
                                                                    Destroyable.DestroyMethod.PHYSICS, null, null)))
                block.setType(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        Block block = event.getBlock();
        if (MainManager.hasBlock(block.getLocation())) {
            BlockBase instance = MainManager.getBlockInstance(block.getLocation());
            event.setCancelled(true);
            if (!(instance instanceof Destroyable &&
                  ((Destroyable) instance).onBlockDestroy(block, Destroyable.DestroyMethod.DECAY, null, null)))
                block.setType(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        if (MainManager.hasBlock(block.getLocation())) {
            BlockBase instance = MainManager.getBlockInstance(block.getLocation());
            event.setCancelled(true);
            if (!(instance instanceof Destroyable &&
                  ((Destroyable) instance).onBlockDestroy(block, Destroyable.DestroyMethod.FIRE, null, null)))
                block.setType(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        if (MainManager.hasBlock(block.getLocation())) {
            BlockBase instance = MainManager.getBlockInstance(block.getLocation());
            event.setCancelled(true);
            if (event.getNewState().getType() == Material.AIR && !(instance instanceof Destroyable &&
                                                                   ((Destroyable) instance).onBlockDestroy(block,
                                                                           Destroyable.DestroyMethod.FADE, null, null)))
                block.setType(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        if (MainManager.hasBlock(block.getLocation())) {
            BlockBase instance = MainManager.getBlockInstance(block.getLocation());
            event.setCancelled(!(instance instanceof Ignitable &&
                                 ((Ignitable) instance).onIgnite(block, event.getCause(), event.getBlock(),
                                         event.getIgnitingEntity())));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (MainManager.hasBlock(block.getLocation())) {
            BlockBase instance = MainManager.getBlockInstance(block.getLocation());
            if (instance instanceof RedstoneChargeable)
                ((RedstoneChargeable) instance).onRedstone(block, event.getOldCurrent(), event.getNewCurrent());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Iterator<Block> iterator = event.blockList().iterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            if (MainManager.hasBlock(block.getLocation())) {
                iterator.remove();
                BlockBase blockBase = MainManager.getBlockInstance(block.getLocation());
                try {
                    if (blockBase instanceof Destroyable &&
                        ((Destroyable) blockBase).onBlockDestroy(block, Destroyable.DestroyMethod.EXPLOSION, null,
                                null)) block.setType(Material.AIR);
                } catch (Throwable t) {
                    WheelCore.getInstance().getLogger()
                            .log(Level.SEVERE, t, () -> "Error while processing block explode event");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Iterator<Block> iterator = event.blockList().iterator(); iterator.hasNext(); ) {
            Block block = iterator.next();
            if (MainManager.hasBlock(block.getLocation())) {
                iterator.remove();
                BlockBase blockBase = MainManager.getBlockInstance(block.getLocation());
                try {
                    if (blockBase instanceof Destroyable &&
                        ((Destroyable) blockBase).onBlockDestroy(block, Destroyable.DestroyMethod.EXPLOSION, null,
                                null)) block.setType(Material.AIR);
                } catch (Throwable t) {
                    WheelCore.getInstance().getLogger()
                            .log(Level.SEVERE, t, () -> "Error while processing entity explode event");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack srcItem = event.getInventory().getItem(0);
        ItemStack result = event.getResult();
        if (srcItem == null || result == null) return;

        Map<Enchantment, Integer> src = srcItem.getEnchantments().entrySet().stream()
                .filter(entry -> entry.getKey() instanceof EnchantmentUtil.CustomEnchantment)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<Enchantment, Integer> conflicts = src.entrySet().stream()
                .filter(entry -> result.getEnchantments().keySet().stream()
                        .anyMatch(enchantment -> entry.getKey().conflictsWith(enchantment)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (conflicts.size() == src.size()) return;

        result.addUnsafeEnchantments(src);
        conflicts.forEach((enchantment, level) -> EnchantmentUtil.removeFromItem(result,
                (EnchantmentUtil.CustomEnchantment) enchantment, level));

        event.setResult(result);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory craftingInv = event.getInventory();
        if (Stream.of(craftingInv.getMatrix()).anyMatch(item -> MainManager.getItemInstance(item) != null) ||
            RecipeRegistry.isPlaceholder(event.getRecipe())) {
            try {
                RecipeBase result = RecipeRegistry.matchCraftingRecipe(null, fillMatrix(craftingInv.getMatrix()), null);
                craftingInv.setResult(result == null ? null : result.getResult());
            } catch (Throwable t) {
                WheelCore.getInstance().getLogger().log(Level.SEVERE, t, () -> "Error while matching crafting recipe");
                craftingInv.setResult(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        CraftingInventory craftingInv = event.getInventory();
        HashMap<Integer, ItemStack> map = new HashMap<>();
        if (Stream.of(craftingInv.getMatrix()).anyMatch(item -> MainManager.getItemInstance(item) != null) ||
            RecipeRegistry.isPlaceholder(event.getRecipe())) {
            if (RecipeRegistry.matchCraftingRecipe(
                    event.getWhoClicked() instanceof Player ? (Player) event.getWhoClicked() : null,
                    fillMatrix(craftingInv.getMatrix()), map) != null) {
                ItemStack[] contents = craftingInv.getStorageContents();
                map.forEach((key, value) -> contents[key] = value);
                Bukkit.getScheduler().runTask(WheelCore.getInstance(), () -> craftingInv.setStorageContents(contents));
            } else event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemSmelt(FurnaceSmeltEvent event) {
        if (MainManager.getItemInstance(event.getSource()) != null) {
            try {
                SmeltingRecipe recipe = RecipeRegistry.matchSmeltingRecipe(event.getSource());
                if (recipe != null) event.setResult(recipe.getResult());
                else event.setCancelled(true);
            } catch (Throwable t) {
                WheelCore.getInstance().getLogger().log(Level.SEVERE, t, () -> "Error while matching smelting recipe");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // item interact priority is higher than blocks
        if (event.hasItem() && event.useItemInHand() != Event.Result.DENY) {
            ItemStack tool = Objects.requireNonNull(event.getItem());
            ItemBase itemBase = MainManager.getItemInstance(tool);
            if (itemBase == null || itemBase instanceof Interactive &&
                                    ((Interactive) itemBase).onInteract(event.getPlayer(), event.getAction(),
                                            event.getHand(), tool, event.getClickedBlock(), null)) return;
            else event.setUseItemInHand(Event.Result.DENY);
        }
        if (event.hasBlock() && event.useInteractedBlock() != Event.Result.DENY) {
            Block block = Objects.requireNonNull(event.getClickedBlock());
            BlockBase blockBase = MainManager.getBlockInstance(block.getLocation());
            if (!(blockBase == null || blockBase instanceof Interactive &&
                                       ((Interactive) blockBase).onInteract(event.getPlayer(), event.getAction(),
                                               event.getHand(), event.getItem(), block, null)))
                event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldInit(WorldInitEvent event) {
        MainManager.onWorldInit(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        for (Entity entity : chunk.getEntities()) {
            MainManager.entityDataProvider.loadEntity(entity);
            Optional.ofNullable(MainManager.entityDataProvider.instanceOf(entity))
                    .ifPresent(base -> base.onLoad(entity));
        }
        MainManager.blockDataProvider.loadChunk(chunk);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnLoad(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        for (Entity entity : chunk.getEntities()) {
            Optional.ofNullable(MainManager.entityDataProvider.instanceOf(entity))
                    .ifPresent(base -> base.onUnload(entity));
            MainManager.entityDataProvider.unloadEntity(entity);
        }
        MainManager.blockDataProvider.unloadChunk(chunk);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent event) {
        if (event.getCommand().equals("reload")) {
            event.setCancelled(true);
            WheelCore.getInstance().getLogger().log(Level.WARNING,
                    "With WheelCore installed, you cannot perform this operation which will cause severe problems. Restart your server to reload plugins instead.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void prePlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equals("/reload") || event.getMessage().equals("/bukkit:reload")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.DARK_RED +
                                          "With WheelCore installed, you cannot perform this operation which will cause severe problems. Restart your server to reload plugins instead.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        ItemBase itemBase = MainManager.getItemInstance(event.getItem());
        if (itemBase == null) {
            return;
        }

        event.setCancelled(!(itemBase instanceof Consumable &&
                             ((Consumable) itemBase).onItemConsume(event.getPlayer(), event.getItem())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemBreak(PlayerItemBreakEvent event) {
        ItemBase itemBase = MainManager.getItemInstance(event.getBrokenItem());
        if (itemBase instanceof Breakable) ((Breakable) itemBase).onItemBreak(event.getPlayer(), event.getBrokenItem());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        ItemBase itemBase = MainManager.getItemInstance(event.getItem());
        if (itemBase instanceof Damageable) {
            try {
                Optional<Integer> result = ((Damageable) itemBase).onItemDamage(event.getPlayer(), event.getItem(),
                        event.getDamage());
                if (result.isPresent()) event.setDamage(result.get());
                else event.setCancelled(true);
            } catch (Throwable t) {
                WheelCore.getInstance().getLogger()
                        .log(Level.SEVERE, t, () -> "Error while processing item damage event");
            }
        } else if (itemBase != null) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        EntityBase instance = MainManager.getEntityInstance(event.getRightClicked());
        if (instance != null) event.setCancelled(!(instance instanceof Interactive &&
                                                   ((Interactive) instance).onInteract(event.getPlayer(),
                                                           Action.RIGHT_CLICK_AIR, event.getHand(),
                                                           event.getPlayer().getInventory().getItemInMainHand(), null,
                                                           event.getRightClicked())));
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event instanceof CreatureSpawnEvent &&
            ((CreatureSpawnEvent) event).getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN) return;
        Entity entity = event.getEntity();
        MainManager.entityDataProvider.loadEntity(entity);
        Optional.ofNullable(MainManager.entityDataProvider.instanceOf(entity)).ifPresent(base -> base.onSpawn(entity));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Optional.ofNullable(MainManager.entityDataProvider.instanceOf(entity)).ifPresent(base -> base.onDeath(entity));
        MainManager.entityDataProvider.unloadEntity(entity);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL ||
            event.getPlayer().getGameMode() == GameMode.ADVENTURE) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLocaleChange(PlayerLocaleChangeEvent event) {
        Bukkit.getScheduler().runTask(WheelCore.getInstance(), () -> event.getPlayer().updateInventory());
    }

    private final static EnumSet<EntityDamageEvent.DamageCause> ARMOR_IGNORE_CAUSES = EnumSet.of(
            EntityDamageEvent.DamageCause.VOID, EntityDamageEvent.DamageCause.SUICIDE,
            EntityDamageEvent.DamageCause.STARVATION, EntityDamageEvent.DamageCause.FALL,
            EntityDamageEvent.DamageCause.FIRE_TICK, EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.CRAMMING, EntityDamageEvent.DamageCause.CUSTOM,
            EntityDamageEvent.DamageCause.DRAGON_BREATH, EntityDamageEvent.DamageCause.DROWNING,
            EntityDamageEvent.DamageCause.SUFFOCATION, EntityDamageEvent.DamageCause.MAGIC,
            EntityDamageEvent.DamageCause.POISON, EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.THORNS, EntityDamageEvent.DamageCause.FLY_INTO_WALL,
            EntityDamageEvent.DamageCause.LIGHTNING);

    static {
        if (Arrays.stream(EntityDamageEvent.DamageCause.values()).anyMatch(cause -> cause.name().equals("FREEZE")))
            ARMOR_IGNORE_CAUSES.add(EntityDamageEvent.DamageCause.FREEZE);
    }

    private static boolean isIgnoreArmor(EntityDamageEvent event) {
        if (ARMOR_IGNORE_CAUSES.contains(event.getCause())) return true;
        if (event.getCause() == EntityDamageEvent.DamageCause.CONTACT && event instanceof EntityDamageByBlockEvent be) {
            Block damager = be.getDamager();
            return damager != null && damager.getType().name().equals("POINTED_DRIPSTONE");
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
            event instanceof EntityDamageByEntityEvent ee)
            return ee.getDamager().getType() == EntityType.AREA_EFFECT_CLOUD;
        return false;
    }

    private final static EnumSet<EntityDamageEvent.DamageCause> RESISTANCE_IGNORE_CAUSES = EnumSet.of(
            EntityDamageEvent.DamageCause.VOID, EntityDamageEvent.DamageCause.SUICIDE,
            EntityDamageEvent.DamageCause.STARVATION);

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        EntityDamageHandler.DamageInfo info = new EntityDamageHandler.DamageInfo();
        info.damage = event.getDamage();
        boolean canBlock = false;
        boolean ignoreArmor = isIgnoreArmor(event);
        if (event.isApplicable(EntityDamageEvent.DamageModifier.HARD_HAT)) {
            info.applyHardHat = true;
            event.setDamage(EntityDamageEvent.DamageModifier.HARD_HAT, 0);
        }
        if (event.isApplicable(EntityDamageEvent.DamageModifier.BLOCKING)) {
            info.applyBlocking = true;
            canBlock = event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) > 0;
            event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
        }
        if (event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) {
            if (!ignoreArmor) info.applyArmor = true;
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        }
        if (event.isApplicable(EntityDamageEvent.DamageModifier.RESISTANCE)) {
            if (!RESISTANCE_IGNORE_CAUSES.contains(event.getCause())) info.applyResistance = true;
            event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, 0);
        }
        if (event.isApplicable(EntityDamageEvent.DamageModifier.MAGIC)) {
            info.applyMagic = true;
            event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
        }
        if (event.isApplicable(EntityDamageEvent.DamageModifier.ABSORPTION)) {
            info.applyAbsorption = true;
            event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);
        }

        switch (event.getCause()) {
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> {
                EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) event;
                if (event1.getDamager() instanceof LivingEntity living) {
                    EntityEquipment equip = living.getEquipment();
                    if (equip == null) break;
                    ItemStack weapon = equip.getItemInMainHand();
                    if (MainManager.getItemInstance(weapon) instanceof Weapon instance) {
                        Optional<EntityDamageHandler.DamageInfo> tmp = instance.getDamage(living, weapon,
                                event.getEntity(), event.getCause());
                        if (tmp.isPresent()) {
                            info = tmp.get();

                            if (event.getEntity() instanceof Wolf && !(living instanceof Player))
                                info.damage = (info.damage + 1) / 2; // Wolf damage reduction

                            // The damage reduction of ender dragon (non-head part) is currently impossible due to the limitation of bukkit API.
                        } else {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        Entity damagerEntity;
        if (event instanceof EntityDamageByEntityEvent event1) damagerEntity = event1.getDamager();
        else damagerEntity = null;
        Block damagerBlock;
        if (event instanceof EntityDamageByBlockEvent event1) damagerBlock = event1.getDamager();
        else damagerBlock = null;
        EntityDamageHandler.ModifyResult result = EntityDamageHandler.calculateModify(event.getCause(), info, canBlock,
                damagerEntity, damagerBlock, event.getEntity());

        event.setDamage(EntityDamageEvent.DamageModifier.BASE, result.baseDamage);
        if (info.applyHardHat) event.setDamage(EntityDamageEvent.DamageModifier.HARD_HAT, result.hardHat);
        if (info.applyBlocking) event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, result.blocking);
        if (info.applyArmor) {
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, result.armor);
            boolean isFire = event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                             event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                             event.getCause() == EntityDamageEvent.DamageCause.LAVA ||
                             event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR ||
                             (damagerEntity != null && damagerEntity.getType() == EntityType.SMALL_FIREBALL);
            if (ignoreArmor && event.getEntity() instanceof HumanEntity)
                new CraftHumanEntity(event.getEntity()).getHandle().getInventory()
                        .costDurability(isFire ? DamageSource.LAVA_SOURCE : DamageSource.GENERIC_SOURCE,
                                (float) (event.getDamage() +
                                         event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) +
                                         event.getDamage(EntityDamageEvent.DamageModifier.HARD_HAT)),
                                PlayerInventory.EQUIPMENT_SLOTS);
        }
        if (info.applyResistance) event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, result.resistance);
        if (info.applyMagic) event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, result.magic);
        if (info.applyAbsorption) event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, result.absorption);
    }
}