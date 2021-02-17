package io.github.plugindustry.wheelcore.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
import io.github.plugindustry.wheelcore.world.NormalOrePopulator;
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

import java.util.HashMap;
import java.util.Set;

public class MainManager {
    public static final HashMap<Base, Set<Location>> baseBlocks = new HashMap<>();
    private static final BiMap<String, Base> mapping = HashBiMap.create();
    public static DataProvider dataProvider = DataProvider.defaultProvider(mapping);
    //private static final HashMap<Location, Map.Entry<String, BlockData>> blocks = new HashMap<>();
    //private static final HashSet<Long> loadedChunks = new HashSet<>();

    // returns if the event doesn't need to be cancelled
    public static boolean processBlockPlacement(ItemStack item, Block newBlock) {
        BlockBase blockBase = (BlockBase) getInstanceFromId(ItemStackUtil.getPIItemId(item));
        return blockBase instanceof Placeable && ((Placeable) blockBase).onBlockPlace(item, newBlock);
    }

    public static boolean processBlockDestroy(ItemStack tool, Block target, Destroyable.DestroyMethod method) {
        BlockBase blockBase = (BlockBase) getBlockInstance(target.getLocation());
        return blockBase instanceof Destroyable && ((Destroyable) blockBase).onBlockDestroy(target, tool, method);
    }

    public static boolean processBlockInteract(Player player, Block block, ItemStack tool, Action action) {
        BlockBase blockBase = (BlockBase) getBlockInstance(block.getLocation());
        if (blockBase == null) {
            return true;
        }
        return blockBase instanceof Interactive && ((Interactive) blockBase).onInteract(player,
                                                                                        action,
                                                                                        tool,
                                                                                        block,
                                                                                        Interactive.InteractActor.BLOCK);
    }

    public static boolean processItemInteract(Player player, Block block, ItemStack tool, Action action) {
        ItemBase itemBase = getItemInstance(tool);
        if (itemBase == null) {
            return true;
        }

        return itemBase instanceof Interactive && ((Interactive) itemBase).onInteract(player,
                                                                                      action,
                                                                                      tool,
                                                                                      block,
                                                                                      Interactive.InteractActor.ITEM);
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
        //loadedChunks.add(convert(chunk.getX(), chunk.getZ()));
        dataProvider.loadChunk(chunk);
    }

    public static void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        //loadedChunks.remove(convert(chunk.getX(), chunk.getZ()));
        dataProvider.unloadChunk(chunk);
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

    public static void loadBlocks() {
        /*for (String str : config.getKeys(false)) {
            List<?> list = config.getList(str);
            String[] arr = StringUtils.split(str, ";");
            addBlock((String) (list.get(0)),
                     new Location(Bukkit.getWorld(arr[0]),
                                  new Integer(arr[1]),
                                  new Integer(arr[2]),
                                  new Integer(arr[3])),
                     (BlockData) (list.get(1)));
        }*/
        dataProvider = DataProvider.defaultProvider(mapping);
    }

    public static void saveBlocks() {
        /*for (Map.Entry<Location, Map.Entry<String, BlockData>> entry : blocks.entrySet()) {
            Location loc = entry.getKey();
            DebuggingLogger.debug("Save " + entry.getValue().getKey() + " to " + loc.getWorld());
            if (loc.getWorld() == null)
                continue;
            config.set(loc.getWorld().getName() +
                       ";" +
                       (int) loc.getX() +
                       ";" +
                       (int) loc.getY() +
                       ";" +
                       (int) loc.getZ(), Arrays.asList(entry.getValue().getKey(), entry.getValue().getValue()));
        }*/
        dataProvider.saveAll();
    }

    public static void addBlock(Location block, Base instance, BlockData data) {
        DebuggingLogger.debug("Block at " + block.toString());
        /*blocks.put(block, new AbstractMap.SimpleEntry<>(id, data));
        Base instance = getInstanceFromId(id);
        if (baseBlocks.containsKey(instance))
            baseBlocks.get(instance).add(block);
        else
            baseBlocks.put(instance, new HashSet<>(Collections.singleton(block)));*/
        dataProvider.addBlock(block, instance, data);
    }

    public static void removeBlock(Location block) {
        //baseBlocks.get(getInstanceFromId(getBlockId(block))).remove(block);
        //blocks.remove(block);
        dataProvider.removeBlock(block);
    }

    public static boolean hasBlock(Location block) {
        //return blocks.containsKey(block);
        return dataProvider.hasBlock(block);
    }

    public static Base getBlockInstance(Location block) {
        /*if (block == null || blocks.get(block) == null) {
            return null;
        }

        return blocks.get(block).getKey();*/
        return dataProvider.instanceAt(block);
    }

    private static ItemBase getItemInstance(ItemStack is) {
        if (!ItemStackUtil.isPIItem(is))
            return null;
        return (ItemBase) getInstanceFromId(ItemStackUtil.getPIItemId(is));
    }

    public static BlockData getBlockData(Location block) {
        //return blocks.get(block).getValue();
        return dataProvider.dataAt(block);
    }

    public static void setBlockData(Location block, BlockData data) {
        //blocks.get(block).setValue(data);
        dataProvider.setDataAt(block, data);
    }

    public static void register(String id, Base b) {
        mapping.put(id, b);
    }
}
