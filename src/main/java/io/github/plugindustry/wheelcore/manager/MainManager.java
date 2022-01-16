package io.github.plugindustry.wheelcore.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import io.github.plugindustry.wheelcore.interfaces.Base;
import io.github.plugindustry.wheelcore.interfaces.Tickable;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityBase;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityData;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.interfaces.item.ItemData;
import io.github.plugindustry.wheelcore.manager.data.block.BlockDataProvider;
import io.github.plugindustry.wheelcore.manager.data.entity.EntityDataProvider;
import io.github.plugindustry.wheelcore.manager.data.item.ItemDataProvider;
import io.github.plugindustry.wheelcore.utils.DebuggingLogger;
import io.github.plugindustry.wheelcore.world.EndPopulator;
import io.github.plugindustry.wheelcore.world.NetherPopulator;
import io.github.plugindustry.wheelcore.world.OverworldPopulator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

public class MainManager {
    private static final BiMap<String, BlockBase> blockMapping = HashBiMap.create();
    private static final BiMap<String, ItemBase> itemMapping = HashBiMap.create();
    private static final BiMap<String, EntityBase> entityMapping = HashBiMap.create();
    public static BlockDataProvider blockDataProvider = BlockDataProvider.defaultProvider();
    public static EntityDataProvider entityDataProvider = EntityDataProvider.defaultProvider();
    public static ItemDataProvider itemDataProvider = ItemDataProvider.defaultProvider();
    private static final Queue<Runnable> postTickTasks = new ArrayDeque<>();

    public static void update() {
        PlayerDigHandler.onTick();

        MultiBlockManager.onTick();

        for (BlockBase base : blockMapping.values())
            if (base instanceof Tickable)
                ((Tickable) base).onTick();

        for (EntityBase base : entityMapping.values())
            if (base instanceof Tickable)
                ((Tickable) base).onTick();

        for (ItemBase base : itemMapping.values())
            if (base instanceof Tickable)
                ((Tickable) base).onTick();

        PowerManager.onTick();

        while (!postTickTasks.isEmpty())
            postTickTasks.poll().run();
    }

    /**
     * @param task The task that need to be executed at the end of tick
     */
    public void queuePostTickTask(@Nonnull Runnable task) {
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
    public static String getIdFromInstance(@Nullable Base instance) {
        if (instance instanceof BlockBase)
            return blockMapping.inverse().getOrDefault(instance, null);
        else if (instance instanceof ItemBase)
            return itemMapping.inverse().getOrDefault(instance, null);
        else if (instance instanceof EntityBase)
            return entityMapping.inverse().getOrDefault(instance, null);
        else
            return null;
    }

    @Nullable
    public static BlockBase getBlockInstanceFromId(@Nullable String id) {
        if (id == null)
            return null;
        return blockMapping.getOrDefault(id, null);
    }

    @Nullable
    public static ItemBase getItemInstanceFromId(@Nullable String id) {
        if (id == null)
            return null;
        return itemMapping.getOrDefault(id, null);
    }

    @Nullable
    public static EntityBase getEntityInstanceFromId(@Nullable String id) {
        if (id == null)
            return null;
        return entityMapping.getOrDefault(id, null);
    }

    public static void save() {
        entityDataProvider.beforeSave();
        blockDataProvider.beforeSave();
        Bukkit.getWorlds().forEach(World::save);
        blockDataProvider.afterSave();
        entityDataProvider.afterSave();
    }

    public static void addBlock(Location block, BlockBase instance, BlockData data) {
        DebuggingLogger.debug("Block at " + block.toString());
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

    public static void registerBlock(@Nonnull String id, @Nonnull BlockBase b) {
        blockMapping.put(id, b);
    }

    public static void registerItem(@Nonnull String id, @Nonnull ItemBase b) {
        itemMapping.put(id, b);
    }

    public static void registerEntity(@Nonnull String id, @Nonnull EntityBase b) {
        entityMapping.put(id, b);
    }

    @Nonnull
    public static BiMap<String, BlockBase> getBlockMapping() {
        return Maps.unmodifiableBiMap(blockMapping);
    }

    @Nonnull
    public static BiMap<String, ItemBase> getItemMapping() {
        return Maps.unmodifiableBiMap(itemMapping);
    }

    @Nonnull
    public static BiMap<String, EntityBase> getEntityMapping() {
        return Maps.unmodifiableBiMap(entityMapping);
    }
}
