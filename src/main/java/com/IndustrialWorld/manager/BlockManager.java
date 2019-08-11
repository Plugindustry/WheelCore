package com.IndustrialWorld.manager;

import com.IndustrialWorld.event.ProcessInfo;
import com.IndustrialWorld.interfaces.BlockBase;
import com.IndustrialWorld.interfaces.BlockData;
import com.IndustrialWorld.utils.NBTUtil;
import com.sun.istack.internal.Nullable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.*;

public class BlockManager {
    private static HashMap<String, BlockBase> mapping = new HashMap<>();
    private static LinkedHashMap<Location, Map.Entry<String, BlockData>> blocks = new LinkedHashMap<>();

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
        for(String str : config.getKeys(false)){
            List list = config.getList(str);
            String[] arr = StringUtils.split(str, ";");
            blocks.put(new Location(Bukkit.getWorld(arr[0]), new Integer(arr[1]), new Integer(arr[2]), new Integer(arr[3])), new AbstractMap.SimpleEntry<String, BlockData>((String) (list.get(0)), (BlockData) (list.get(1))));
        }
    }

    public static void saveBlocksToConfig(YamlConfiguration config){
        for(Map.Entry<Location, Map.Entry<String, BlockData>> entry : blocks.entrySet()){
            Location loc = entry.getKey();
            config.set(loc.getWorld().getName() + ";" + (int) loc.getX() + ";" + (int) loc.getY() + ";" + (int) loc.getZ(), Arrays.asList(entry.getValue().getKey(), entry.getValue().getValue()));
        }
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
