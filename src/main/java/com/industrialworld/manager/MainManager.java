package com.industrialworld.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.industrialworld.interfaces.Base;
import com.industrialworld.interfaces.Interactive;
import com.industrialworld.interfaces.Tickable;
import com.industrialworld.interfaces.block.BlockBase;
import com.industrialworld.interfaces.block.BlockData;
import com.industrialworld.interfaces.block.Destoryable;
import com.industrialworld.interfaces.block.Placeable;
import com.industrialworld.interfaces.item.ItemBase;
import com.industrialworld.utils.DebuggingLogger;
import com.industrialworld.utils.NBTUtil;
import com.industrialworld.world.NormalOrePopulator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MainManager {
    public static final HashMap<Base, Set<Location>> baseBlocks = new HashMap<>();
    private static final BiMap<String, Base> mapping = HashBiMap.create();
    private static final HashMap<Location, Map.Entry<String, BlockData>> blocks = new HashMap<>();
    private static final HashSet<Long> loadedChunks = new HashSet<>();

    // returns if the event doesn't need to be cancelled
    public static boolean processBlockPlacement(ItemStack item, Block newBlock) {
        BlockBase blockBase = (BlockBase) getInstanceFromId(NBTUtil.getTagValue(item, "IWItemId").asString());
        return blockBase instanceof Placeable && ((Placeable) blockBase).onBlockPlace(newBlock);
    }

    public static boolean processBlockDestroy(ItemStack tool, Block target, boolean canceled) {
        BlockBase blockBase = (BlockBase) getInstanceFromId(getBlockId(target.getLocation()));
        return blockBase instanceof Destoryable && ((Destoryable) blockBase).onBlockDestroy(target, tool, canceled);
    }

    public static boolean processBlockInteract(Player player, Block block, ItemStack tool, Action action) {
        BlockBase blockBase = (BlockBase) getInstanceFromId(getBlockId(block.getLocation()));
        if (blockBase == null) {
            return true;
        }
        return blockBase instanceof Interactive &&
               ((Interactive) blockBase).onInteract(player, action, tool, block, Interactive.InteractActor.BLOCK);
    }

    public static boolean processItemInteract(Player player, Block block, ItemStack tool, Action action) {
        ItemBase itemBase = getItemInstance(tool);
        if (itemBase == null) {
            return true;
        }

        return itemBase instanceof Interactive &&
               ((Interactive) itemBase).onInteract(player, action, tool, block, Interactive.InteractActor.ITEM);
    }

    public static void update() {
        for (Base base : mapping.values())
            if (base instanceof Tickable)
                ((Tickable) base).onTick();
    }

    public static void onWorldInit(WorldInitEvent event) {
        // TODO: world name comparison
        if (event.getWorld().getEnvironment() == World.Environment.NORMAL) {
            event.getWorld().getPopulators().add(new NormalOrePopulator());
        } else if (event.getWorld().getEnvironment() == World.Environment.NETHER) {
            // TODO: Nether Ore Populate
        } else if (event.getWorld().getEnvironment() == World.Environment.THE_END) {
            // TODO: The End Ore Populate
        }
    }

    public static void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        loadedChunks.add((((long) chunk.getX()) << 32) | chunk.getZ());
    }

    public static void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        loadedChunks.remove((((long) chunk.getX()) << 32) | chunk.getZ());
    }

    public static String getIdFromInstance(Base instance) {
        if (instance == null)
            return "";
        return mapping.inverse().get(instance);
    }

    public static Base getInstanceFromId(String id) {
        if (id == null)
            return null;
        return mapping.get(id);
    }

    public static void loadBlocksFromConfig(YamlConfiguration config) {
        for (String str : config.getKeys(false)) {
            List<?> list = config.getList(str);
            String[] arr = StringUtils.split(str, ";");
            addBlock((String) (list.get(0)), new Location(Bukkit.getWorld(arr[0]), new Integer(arr[1]), new Integer(arr[2]), new Integer(arr[3])), (BlockData) (list.get(1)));
        }
    }

    public static void saveBlocksToConfig(YamlConfiguration config) {
        for (Map.Entry<Location, Map.Entry<String, BlockData>> entry : blocks.entrySet()) {
            Location loc = entry.getKey();
            DebuggingLogger.debug("Save " + entry.getValue().getKey() + " to " + loc.getWorld());
            if (loc.getWorld() == null)
                continue;
            config.set(loc.getWorld().getName() + ";" + (int) loc.getX() + ";" + (int) loc.getY() + ";" +
                       (int) loc.getZ(), Arrays.asList(entry.getValue().getKey(), entry.getValue().getValue()));
        }
    }

    public static void addBlock(String id, Location block, BlockData data) {
        DebuggingLogger.debug("Block at " + block.toString());
        blocks.put(block, new AbstractMap.SimpleEntry<>(id, data));
        Base instance = getInstanceFromId(id);
        if (baseBlocks.containsKey(instance))
            baseBlocks.get(instance).add(block);
        else
            baseBlocks.put(instance, new HashSet<>(Collections.singleton(block)));
    }

    public static void removeBlock(Location block) {
        baseBlocks.get(getInstanceFromId(getBlockId(block))).remove(block);
        blocks.remove(block);
    }

    public static boolean hasBlock(Location block) {
        return blocks.containsKey(block);
    }

    public static String getBlockId(Location block) {
        if (block == null || blocks.get(block) == null) {
            return null;
        }

        return blocks.get(block).getKey();
    }

    private static ItemBase getItemInstance(ItemStack is) {
        NBTUtil.NBTValue value = NBTUtil.getTagValue(is, "IWItemId");
        if (value == null)
            return null;
        try {
            return (ItemBase) getInstanceFromId(value.asString());
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static BlockData getBlockData(Location block) {
        return blocks.get(block).getValue();
    }

    public static void setBlockData(Location block, BlockData data) {
        blocks.get(block).setValue(data);
    }

    public static boolean isBlockActive(Location block) {
        int x = block.getBlockX() >> 4;
        int z = block.getBlockZ() >> 4;
        return loadedChunks.contains((((long) x) << 32) | z);
    }

    public static void register(String id, Base b) {
        mapping.put(id, b);
    }
}
