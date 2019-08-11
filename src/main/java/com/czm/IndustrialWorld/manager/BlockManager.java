package com.czm.IndustrialWorld.manager;

import com.czm.IndustrialWorld.event.ProcessInfo;
import com.czm.IndustrialWorld.interfaces.BlockBase;
import com.czm.IndustrialWorld.interfaces.BlockData;
import com.czm.IndustrialWorld.utils.NBTUtil;
import com.sun.istack.internal.Nullable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BlockManager {
    private static HashMap<String, BlockBase> mapping = new HashMap<>();
    private static LinkedHashMap<Location, Map.Entry<String, BlockData>> blocks;

    public static void process(BlockEvent event) {
        if (event instanceof BlockPlaceEvent)
            getInstanceFromId(NBTUtil.getTagValue(((BlockPlaceEvent) event).getItemInHand(), "IWItemId").asString()).onPlace((BlockPlaceEvent) event, new ProcessInfo());
        else if (event instanceof BlockBreakEvent)
            getInstanceFromId(getBlockId(event.getBlock())).onBreak((BlockBreakEvent) event);
    }

    public static String getIdFromInstance(BlockBase instance) {
        for (Map.Entry<String, BlockBase> e : mapping.entrySet())
            if (e.getValue().equals(instance))
                return e.getKey();
        return "";
    }

    public static BlockBase getInstanceFromId(String id) {
        return mapping.get(id);
    }

    public static void loadBlocksFromConfig(YamlConfiguration config){
        blocks = (config.get("blocks") == null ? new LinkedHashMap<>() : ((LinkedHashMap<Location, Map.Entry<String, BlockData>>) config.get("blocks")));
    }

    public static void saveBlocksToConfig(YamlConfiguration config){
        config.set("blocks", blocks);
    }

    public static void addBlock(String id, Block block, @Nullable BlockData data) {
        blocks.put(block.getLocation(), new AbstractMap.SimpleEntry<>(id, data));
    }

    public static void removeBlock(Block block) {
        blocks.remove(block.getLocation());
    }

    public static boolean hasBlock(Block block) {
        return blocks.containsKey(block.getLocation());
    }

    public static String getBlockId(Block block) {
        return blocks.get(block.getLocation()).getKey();
    }

    public static BlockData getBlockData(Block block) {
        return blocks.get(block.getLocation()).getValue();
    }

    public static void register(String id, BlockBase b){
        mapping.put(id, b);
    }
}
