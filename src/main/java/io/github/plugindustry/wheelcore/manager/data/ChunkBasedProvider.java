package io.github.plugindustry.wheelcore.manager.data;

import com.google.common.collect.BiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import io.github.plugindustry.wheelcore.utils.DebuggingLogger;
import io.github.plugindustry.wheelcore.utils.NBTUtil;
import io.github.plugindustry.wheelcore.utils.NBTUtil.NBTValue;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ChunkBasedProvider implements DataProvider {
    private final BiMap<String, BlockBase> mapping;
    private final HashMap<BlockBase, Set<Location>> baseBlocks = new HashMap<>();
    private final HashMap<Location, Map.Entry<BlockBase, BlockData>> blocks = new HashMap<>();
    private final HashMap<World, HashSet<Long>> loadedChunks = new HashMap<>();
    private final Gson gson = new Gson();

    public ChunkBasedProvider(BiMap<String, BlockBase> mapping) {
        this.mapping = mapping;
    }

    private static long chunkDescAt(Location loc) {
        return compress(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    private static long compress(int i1, int i2) {
        return ((i1 & 0x00000000ffffffffL) << 32) | (i2 & 0x00000000ffffffffL);
    }

    private static Map.Entry<Integer, Integer> decompress(long l) {
        return new AbstractMap.SimpleEntry<>((int) (l & 0xffffffff00000000L), (int) (l & 0x00000000ffffffffL));
    }

    @Override
    public BlockData dataAt(Location loc) {
        return blocks.get(loc).getValue();
    }

    @Override
    public void setDataAt(Location loc, BlockData data) {
        blocks.get(loc).setValue(data);
    }

    @Override
    public BlockBase instanceAt(Location loc) {
        return blocks.containsKey(loc) ? blocks.get(loc).getKey() : null;
    }

    @Override
    public boolean hasBlock(Location block) {
        return blocks.containsKey(block);
    }

    @Override
    public void addBlock(Location block, BlockBase instance, BlockData data) {
        if (!baseBlocks.containsKey(instance))
            baseBlocks.put(instance, new HashSet<>());
        baseBlocks.get(instance).add(block);
        blocks.put(block, new AbstractMap.SimpleEntry<>(instance, data));
    }

    @Override
    public void removeBlock(Location block) {
        BlockBase base = instanceAt(block);
        baseBlocks.get(base).remove(block);
        blocks.remove(block);
    }

    @Override
    public void loadChunk(Chunk chunk) {
        DebuggingLogger.debug("Load chunk " + chunk.getX() + " " + chunk.getZ());
        loadedChunks.putIfAbsent(chunk.getWorld(), new HashSet<>());
        loadedChunks.get(chunk.getWorld()).add(compress(chunk.getX(), chunk.getZ()));

        Block dataBlock = chunk.getBlock(0, 0, 0);
        if (dataBlock.getType() != Material.JUKEBOX)
            return;

        ItemStack dataItem = ((Jukebox) dataBlock.getState()).getRecord();
        NBTValue value = NBTUtil.getTagValue(dataItem, "data");
        if (value == null)
            return;
        DebuggingLogger.debug(value.asString());
        List<BlockDescription> blockList = gson.fromJson(value.asString(), new TypeToken<List<BlockDescription>>() {
        }.getType());
        for (BlockDescription desc : blockList)
            addBlock(new Location(Bukkit.getWorld(desc.worldName), desc.x, desc.y, desc.z),
                     mapping.get(desc.id),
                     desc.data);

        dataBlock.setType(Material.BEDROCK);
    }

    @Override
    public void unloadChunk(Chunk chunk) {
        DebuggingLogger.debug("Unload chunk " + chunk.getX() + " " + chunk.getZ());
        long chunkDesc = compress(chunk.getX(), chunk.getZ());
        if (loadedChunks.containsKey(chunk.getWorld()) && loadedChunks.get(chunk.getWorld()).contains(chunkDesc)) {
            DebuggingLogger.debug("Save chunk " + chunk.getX() + " " + chunk.getZ());
            LinkedList<BlockDescription> descList = new LinkedList<>();
            LinkedList<Location> locations = new LinkedList<>();
            blocks.keySet().stream().filter(loc -> chunkDescAt(loc) == chunkDesc).forEach(loc -> {
                descList.add(new BlockDescription(loc,
                                                  mapping.inverse().get(blocks.get(loc).getKey()),
                                                  blocks.get(loc).getValue()));
                locations.add(loc);
            });
            locations.forEach(this::removeBlock);
            if (!descList.isEmpty()) {
                Block dataBlock = chunk.getBlock(0, 0, 0);
                dataBlock.setType(Material.JUKEBOX);
                String json = gson.toJson(descList);
                DebuggingLogger.debug(json);
                ((Jukebox) dataBlock.getState()).setRecord(NBTUtil.setTagValue(new ItemStack(Material.PAPER),
                                                                               "data",
                                                                               NBTValue.of(json)));
            }
            loadedChunks.get(chunk.getWorld()).remove(chunkDesc);
        }
    }

    @Override
    public void saveAll() {
        for (Map.Entry<World, HashSet<Long>> entry : loadedChunks.entrySet()) {
            HashSet<Long> temp = (HashSet<Long>) entry.getValue().clone();
            for (long chunkDesc : temp) {
                Map.Entry<Integer, Integer> decompressed = decompress(chunkDesc);
                unloadChunk(entry.getKey().getChunkAt(decompressed.getKey(), decompressed.getValue()));
            }
            entry.getKey().save();
            for (long chunkDesc : temp) {
                Map.Entry<Integer, Integer> decompressed = decompress(chunkDesc);
                loadChunk(entry.getKey().getChunkAt(decompressed.getKey(), decompressed.getValue()));
            }
        }
        Bukkit.getWorlds().stream().filter(world -> !loadedChunks.containsKey(world)).forEach(World::save);
    }

    @Override
    public Set<Location> blocks() {
        return Collections.unmodifiableSet(blocks.keySet());
    }

    @Override
    public Set<Location> blocksOf(BlockBase base) {
        return Collections.unmodifiableSet(baseBlocks.get(base));
    }

    private static class BlockDescription {
        String worldName;
        int x;
        int y;
        int z;
        String id;
        BlockData data;

        BlockDescription(Location loc, String id, BlockData data) {
            this.worldName = Objects.requireNonNull(loc.getWorld()).getName();
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
            this.id = id;
            this.data = data;
        }
    }
}
