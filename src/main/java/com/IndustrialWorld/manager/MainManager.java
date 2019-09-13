package com.IndustrialWorld.manager;

import com.IndustrialWorld.event.TickEvent;
import com.IndustrialWorld.interfaces.Base;
import com.IndustrialWorld.interfaces.BlockBase;
import com.IndustrialWorld.interfaces.BlockData;
import com.IndustrialWorld.interfaces.ItemBase;
import com.IndustrialWorld.utils.NBTUtil;
import com.IndustrialWorld.world.NormalOrePopulator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MainManager {
    private static HashMap<String, Base> mapping = new HashMap<>();
    private static LinkedHashMap<Location, Map.Entry<String, BlockData>> blocks = new LinkedHashMap<>();

    // returns if the event needs to be cancelled
    public static boolean processBlockPlacement(ItemStack item, Block newBlock) {
    	BlockBase blockBase = (BlockBase) getInstanceFromId(NBTUtil.getTagValue(item, "IWItemId").asString());
    	return blockBase.onBlockPlace(newBlock);
    }

    public static boolean processBlockDestroy(ItemStack tool, Block target, boolean canceled) {
		BlockBase blockBase = (BlockBase) getInstanceFromId(getBlockId(target));
		return blockBase.onBlockDestroy(target, tool, canceled);
    }

    public static boolean processBlockInteract(Player player, Block block, ItemStack tool, Action action) {
    	BlockBase blockBase = (BlockBase) getInstanceFromId(getBlockId(block));
    	if (blockBase == null) {
    		return true;
	    }
    	return blockBase.onInteract(player, action, tool, block);
    }

    public static boolean processItemInteract(Player player, Block block, ItemStack tool, Action action) {
    	ItemBase itemBase = getItemInstance(tool);
    	if (itemBase == null) {
    		return true;
	    }

    	return itemBase.onInteract(player, action, tool, block);
    }

    public static void update(TickEvent tick) {
		for (Base base : mapping.values()) {
			base.onTick(tick);
		}
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

	public static String getIdFromInstance(Base instance) {
        for (Map.Entry<String, Base> e : mapping.entrySet())
            if (e.getValue().equals(instance))
                return e.getKey();
        return "";
    }

    public static Base getInstanceFromId(String id) {
    	if (id == null) {
    		return null;
	    }
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
    	if (block == null || blocks.get(block.getLocation()) == null) {
    		return null;
	    }

        return blocks.get(block.getLocation()).getKey();
    }

    private static ItemBase getItemInstance(ItemStack is) {
	    NBTUtil.NBTValue value = NBTUtil.getTagValue(is, "isIWItem");
	    NBTUtil.NBTValue v2 = NBTUtil.getTagValue(is, "IWItemId");
	    if(v2 == null)
	    	return null;
	    try {
		    return (ItemBase) getInstanceFromId(v2.asString());
	    } catch (ClassCastException e) {
	    	return null;
	    }
    }

    public static BlockData getBlockData(Block block) {
        return blocks.get(block.getLocation()).getValue();
    }

    public static void register(String id, Base b) {
        mapping.put(id, b);
    }
}
