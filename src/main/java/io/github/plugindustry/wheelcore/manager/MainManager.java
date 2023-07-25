package io.github.plugindustry.wheelcore.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.Base;
import io.github.plugindustry.wheelcore.interfaces.Tickable;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityBase;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityData;
import io.github.plugindustry.wheelcore.interfaces.fluid.FluidBase;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.interfaces.item.ItemData;
import io.github.plugindustry.wheelcore.manager.data.block.BlockDataProvider;
import io.github.plugindustry.wheelcore.manager.data.entity.EntityDataProvider;
import io.github.plugindustry.wheelcore.manager.data.item.ItemDataProvider;
import io.github.plugindustry.wheelcore.world.EndPopulator;
import io.github.plugindustry.wheelcore.world.NetherPopulator;
import io.github.plugindustry.wheelcore.world.OverworldPopulator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;

public class MainManager {
    public static final BlockDataProvider blockDataProvider = BlockDataProvider.defaultProvider();
    public static final EntityDataProvider entityDataProvider = EntityDataProvider.defaultProvider();
    public static final ItemDataProvider itemDataProvider = ItemDataProvider.defaultProvider();
    private static final BiMap<NamespacedKey, BlockBase> blockMapping = HashBiMap.create();
    private static final BiMap<NamespacedKey, ItemBase> itemMapping = HashBiMap.create();
    private static final BiMap<NamespacedKey, EntityBase> entityMapping = HashBiMap.create();
    private static final BiMap<NamespacedKey, FluidBase> fluidMapping = HashBiMap.create();
    private static final Queue<Runnable> postTickTasks = new ArrayDeque<>();

    public static void update() {
        PlayerDigHandler.onTick();

        MultiBlockManager.onTick();

        for (BlockBase base : blockMapping.values())
            if (base instanceof Tickable) {
                try {
                    ((Tickable) base).onTick();
                } catch (Throwable t) {
                    WheelCore.getInstance().getLogger().log(Level.SEVERE, t, () -> "Error while ticking blocks");
                }
            }

        for (EntityBase base : entityMapping.values())
            if (base instanceof Tickable) {
                try {
                    ((Tickable) base).onTick();
                } catch (Throwable t) {
                    WheelCore.getInstance().getLogger().log(Level.SEVERE, t, () -> "Error while ticking entities");
                }
            }

        for (ItemBase base : itemMapping.values())
            if (base instanceof Tickable) {
                try {
                    ((Tickable) base).onTick();
                } catch (Throwable t) {
                    WheelCore.getInstance().getLogger().log(Level.SEVERE, t, () -> "Error while ticking items");
                }
            }

        for (FluidBase base : fluidMapping.values())
            if (base instanceof Tickable) {
                try {
                    ((Tickable) base).onTick();
                } catch (Throwable t) {
                    WheelCore.getInstance().getLogger().log(Level.SEVERE, t, () -> "Error while ticking fluids");
                }
            }

        while (!postTickTasks.isEmpty()) {
            try {
                postTickTasks.poll().run();
            } catch (Throwable t) {
                WheelCore.getInstance().getLogger().log(Level.SEVERE, t, () -> "Error while running post tick tasks");
            }
        }
    }

    /**
     * @param task The task that need to be executed at the end of tick
     */
    public static void queuePostTickTask(@Nonnull Runnable task) {
        postTickTasks.add(task);
    }

    public static void onWorldInit(WorldInitEvent event) {
        if (event.getWorld().getEnvironment() == World.Environment.NORMAL) {
            event.getWorld().getPopulators().add(new OverworldPopulator());
        } else if (event.getWorld().getEnvironment() == World.Environment.NETHER) {
            event.getWorld().getPopulators().add(new NetherPopulator());
        } else if (event.getWorld().getEnvironment() == World.Environment.THE_END) {
            event.getWorld().getPopulators().add(new EndPopulator());
        }
    }

    @Nullable
    public static NamespacedKey getIdFromInstance(@Nullable Base instance) {
        if (instance instanceof BlockBase) return blockMapping.inverse().getOrDefault(instance, null);
        else if (instance instanceof ItemBase) return itemMapping.inverse().getOrDefault(instance, null);
        else if (instance instanceof EntityBase) return entityMapping.inverse().getOrDefault(instance, null);
        else if (instance instanceof FluidBase) return fluidMapping.inverse().getOrDefault(instance, null);
        else return null;
    }

    @Nullable
    public static BlockBase getBlockInstanceFromId(@Nullable NamespacedKey id) {
        if (id == null) return null;
        return blockMapping.getOrDefault(id, null);
    }

    @Nullable
    public static ItemBase getItemInstanceFromId(@Nullable NamespacedKey id) {
        if (id == null) return null;
        return itemMapping.getOrDefault(id, null);
    }

    @Nullable
    public static EntityBase getEntityInstanceFromId(@Nullable NamespacedKey id) {
        if (id == null) return null;
        return entityMapping.getOrDefault(id, null);
    }

    @Nullable
    public static FluidBase getFluidInstanceFromId(@Nullable NamespacedKey id) {
        if (id == null) return null;
        return fluidMapping.getOrDefault(id, null);
    }

    public static void save() {
        entityDataProvider.beforeSave();
        blockDataProvider.beforeSave();
        Bukkit.getWorlds().forEach(World::save);
        blockDataProvider.afterSave();
        entityDataProvider.afterSave();
    }

    public static void addBlock(Location block, BlockBase instance, BlockData data) {
        blockDataProvider.addBlock(block, instance, data);
    }

    public static void removeBlock(Location block) {
        blockDataProvider.removeBlock(block);
    }

    public static boolean hasBlock(Location block) {
        return blockDataProvider.hasBlock(block);
    }

    @Nullable
    public static BlockBase getBlockInstance(@Nonnull Location block) {
        return blockDataProvider.instanceAt(block);
    }

    @Nullable
    public static ItemBase getItemInstance(@Nonnull ItemStack is) {
        return itemDataProvider.getInstance(is);
    }

    @Nullable
    public static EntityBase getEntityInstance(@Nonnull Entity entity) {
        return entityDataProvider.instanceOf(entity);
    }

    @Nullable
    public static BlockData getBlockData(@Nonnull Location block) {
        return blockDataProvider.dataAt(block);
    }

    @Nullable
    public static ItemData getItemData(@Nonnull ItemStack is) {
        return itemDataProvider.getData(is);
    }

    @Nonnull
    public static Set<String> getItemOreDictionary(@Nonnull ItemStack is) {
        return itemDataProvider.getOreDictionary(is);
    }

    @Nullable
    public static EntityData getEntityData(@Nonnull Entity entity) {
        return entityDataProvider.getData(entity);
    }

    public static void setBlockData(@Nonnull Location block, @Nullable BlockData data) {
        blockDataProvider.setDataAt(block, data);
    }

    public static void setItemInstance(@Nonnull ItemStack is, @Nullable ItemBase instance) {
        itemDataProvider.setInstance(is, instance);
    }

    public static void setItemData(@Nonnull ItemStack is, @Nullable ItemData data) {
        itemDataProvider.setData(is, data);
    }

    public static void setItemOreDictionary(@Nonnull ItemStack is, @Nonnull Set<String> oreDictionary) {
        itemDataProvider.setOreDictionary(is, oreDictionary);
    }

    public static void setEntityData(@Nonnull Entity entity, @Nullable EntityData data) {
        entityDataProvider.setData(entity, data);
    }

    public static void registerBlock(@Nonnull NamespacedKey id, @Nonnull BlockBase b) {
        blockMapping.put(id, b);
    }

    public static void registerItem(@Nonnull NamespacedKey id, @Nonnull ItemBase b) {
        itemMapping.put(id, b);
    }

    public static void registerEntity(@Nonnull NamespacedKey id, @Nonnull EntityBase b) {
        entityMapping.put(id, b);
    }

    public static void registerFluid(@Nonnull NamespacedKey id, @Nonnull FluidBase b) {
        fluidMapping.put(id, b);
    }

    @Nonnull
    public static BiMap<NamespacedKey, BlockBase> getBlockMapping() {
        return Maps.unmodifiableBiMap(blockMapping);
    }

    @Nonnull
    public static BiMap<NamespacedKey, ItemBase> getItemMapping() {
        return Maps.unmodifiableBiMap(itemMapping);
    }

    @Nonnull
    public static BiMap<NamespacedKey, EntityBase> getEntityMapping() {
        return Maps.unmodifiableBiMap(entityMapping);
    }

    @Nonnull
    public static BiMap<NamespacedKey, FluidBase> getFluidMapping() {
        return Maps.unmodifiableBiMap(fluidMapping);
    }
}