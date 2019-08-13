package com.IndustrialWorld.manager;

import com.IndustrialWorld.event.ProcessInfo;
import com.IndustrialWorld.event.TickEvent;
import com.IndustrialWorld.interfaces.Base;
import com.IndustrialWorld.interfaces.BlockBase;
import com.IndustrialWorld.interfaces.BlockData;
import com.IndustrialWorld.interfaces.ItemBase;
import com.IndustrialWorld.utils.NBTUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class MainManager {
    private static HashMap<String, Base> mapping = new HashMap<>();
    private static LinkedHashMap<Location, Map.Entry<String, BlockData>> blocks = new LinkedHashMap<>();

    public static void process(Event event) {
        if (event instanceof BlockPlaceEvent)
            ((BlockBase) getInstanceFromId(NBTUtil.getTagValue(((BlockPlaceEvent) event).getItemInHand(), "IWItemId").asString())).onPlace((BlockPlaceEvent) event, new ProcessInfo());
        else if (event instanceof BlockBreakEvent)
            ((BlockBase) getInstanceFromId(getBlockId(((BlockBreakEvent) event).getBlock()))).onBreak((BlockBreakEvent) event);
        else if (event instanceof PlayerInteractEvent) {
            if (((PlayerInteractEvent) event).hasBlock() &&
                MainManager.hasBlock(((PlayerInteractEvent) event).getClickedBlock()))
                ((BlockBase) getInstanceFromId(getBlockId(((PlayerInteractEvent) event).getClickedBlock()))).onInteractAsBlock((PlayerInteractEvent) event);
            if (((PlayerInteractEvent) event).hasItem()) {
                NBTUtil.NBTValue value = NBTUtil.getTagValue(((PlayerInteractEvent) event).getItem(), "isIWItem");
                NBTUtil.NBTValue v2 = NBTUtil.getTagValue(((PlayerInteractEvent) event).getItem(), "IWItemId");
                if(v2 == null)
                    return;
                Base instance = getInstanceFromId(v2.asString());
                if (value != null && value.asBoolean() && instance instanceof ItemBase)
                    ((ItemBase) instance).onInteractAsItem((PlayerInteractEvent) event);
            }
        } else if (event instanceof TickEvent)
            for (Base base : mapping.values())
                base.onTick((TickEvent) event);
    }

    public static String getIdFromInstance(Base instance) {
        for (Map.Entry<String, Base> e : mapping.entrySet())
            if (e.getValue().equals(instance))
                return e.getKey();
        return "";
    }

    public static Base getInstanceFromId(String id) {
        return mapping.get(id);
    }

    public static void loadBlocksFromConfig(YamlConfiguration config) {
        for (String str : config.getKeys(false)) {
            List list = config.getList(str);
            String[] arr = StringUtils.split(str, ";");
            blocks.put(new Location(Bukkit.getWorld(arr[0]), new Integer(arr[1]), new Integer(arr[2]), new Integer(arr[3])), new AbstractMap.SimpleEntry<String, BlockData>((String) (list.get(0)), (BlockData) (list.get(1))));
        }
    }

    public static void saveBlocksToConfig(YamlConfiguration config) {
        for (Map.Entry<Location, Map.Entry<String, BlockData>> entry : blocks.entrySet()) {
            Location loc = entry.getKey();
            config.set(loc.getWorld().getName() + ";" + (int) loc.getX() + ";" + (int) loc.getY() + ";" +
                       (int) loc.getZ(), Arrays.asList(entry.getValue().getKey(), entry.getValue().getValue()));
        }
    }

    public static void addBlock(String id, Block block, BlockData data) {
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

    public static void register(String id, Base b) {
        mapping.put(id, b);
    }
}
