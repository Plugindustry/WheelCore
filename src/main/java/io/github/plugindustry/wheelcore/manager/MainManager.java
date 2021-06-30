package io.github.plugindustry.wheelcore.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import io.github.plugindustry.wheelcore.interfaces.Base;
import io.github.plugindustry.wheelcore.interfaces.Interactive;
import io.github.plugindustry.wheelcore.interfaces.Tickable;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import io.github.plugindustry.wheelcore.interfaces.block.Destroyable;
import io.github.plugindustry.wheelcore.interfaces.block.Placeable;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.manager.data.DataProvider;
import io.github.plugindustry.wheelcore.utils.DebuggingLogger;
import io.github.plugindustry.wheelcore.utils.ItemStackUtil;
import io.github.plugindustry.wheelcore.world.EndPopulator;
import io.github.plugindustry.wheelcore.world.NetherPopulator;
import io.github.plugindustry.wheelcore.world.OverworldPopulator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;

public class MainManager {
    private static final BiMap<String, BlockBase> blockMapping = HashBiMap.create();
    private static final BiMap<String, ItemBase> itemMapping = HashBiMap.create();
    public static DataProvider dataProvider = DataProvider.defaultProvider(blockMapping);

    // returns true if the event doesn't need to be cancelled
    public static boolean processBlockPlacement(ItemStack item, Block newBlock) {
        BlockBase blockBase = getBlockInstanceFromId(ItemStackUtil.getPIItemId(item));
        return blockBase instanceof Placeable && ((Placeable) blockBase).onBlockPlace(item, newBlock);
    }

    public static boolean processBlockDestroy(ItemStack tool, Block target, Destroyable.DestroyMethod method) {
        BlockBase blockBase = getBlockInstance(target.getLocation());
        return blockBase instanceof Destroyable && ((Destroyable) blockBase).onBlockDestroy(target, tool, method);
    }

    public static boolean processBlockInteract(Player player, Block block, ItemStack tool, Action action) {
        BlockBase blockBase = getBlockInstance(block.getLocation());
        if (blockBase == null) {
            return true;
        }
        return blockBase instanceof Interactive && ((Interactive) blockBase).onInteract(player, action, tool, block);
    }

    public static boolean processItemInteract(Player player, Block block, ItemStack tool, Action action) {
        ItemBase itemBase = getItemInstance(tool);
        if (itemBase == null) {
            return true;
        }

        return itemBase instanceof Interactive && ((Interactive) itemBase).onInteract(player, action, tool, block);
    }

    public static void update() {
        MultiBlockManager.onTick();

        for (BlockBase base : blockMapping.values())
            if (base instanceof Tickable)
                ((Tickable) base).onTick();

        for (ItemBase base : itemMapping.values())
            if (base instanceof Tickable)
                ((Tickable) base).onTick();

        PowerManager.onTick();
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

    public static void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        dataProvider.loadChunk(chunk);
    }

    public static void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        dataProvider.unloadChunk(chunk);
    }

    public static String getIdFromInstance(Base instance) {
        if (instance instanceof BlockBase)
            return blockMapping.inverse().getOrDefault(instance, null);
        else if (instance instanceof ItemBase)
            return itemMapping.inverse().getOrDefault(instance, null);
        else
            return null;
    }

    public static BlockBase getBlockInstanceFromId(String id) {
        return blockMapping.getOrDefault(id, null);
    }

    public static ItemBase getItemInstanceFromId(String id) {
        if (id == null)
            return null;
        return itemMapping.getOrDefault(id, null);
    }

    public static void load() {
        dataProvider = DataProvider.defaultProvider(blockMapping);
    }

    public static void save() {
        dataProvider.saveAll();
    }

    public static void addBlock(Location block, BlockBase instance, BlockData data) {
        DebuggingLogger.debug("Block at " + block.toString());
        dataProvider.addBlock(block, instance, data);
    }

    public static void removeBlock(Location block) {
        dataProvider.removeBlock(block);
    }

    public static boolean hasBlock(Location block) {
        return dataProvider.hasBlock(block);
    }

    public static BlockBase getBlockInstance(Location block) {
        return dataProvider.instanceAt(block);
    }

    private static ItemBase getItemInstance(ItemStack is) {
        if (!ItemStackUtil.isPIItem(is))
            return null;
        return getItemInstanceFromId(ItemStackUtil.getPIItemId(is));
    }

    public static BlockData getBlockData(Location block) {
        return dataProvider.dataAt(block);
    }

    public static void setBlockData(Location block, BlockData data) {
        dataProvider.setDataAt(block, data);
    }

    public static void registerBlock(String id, BlockBase b) {
        blockMapping.put(id, b);
    }

    public static void registerItem(String id, ItemBase b) {
        itemMapping.put(id, b);
    }

    public static BiMap<String, BlockBase> getBlockMapping() {
        return Maps.unmodifiableBiMap(blockMapping);
    }

    public static BiMap<String, ItemBase> getItemMapping() {
        return Maps.unmodifiableBiMap(itemMapping);
    }
}
