package com.industrialworld.manager.data;

import com.google.common.collect.BiMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.industrialworld.interfaces.Base;
import com.industrialworld.interfaces.block.BlockData;
import com.industrialworld.utils.NBTUtil;
import com.industrialworld.utils.NBTUtil.NBTValue;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChunkBasedProvider implements DataProvider {
    private final BiMap<String, Base> mapping;
    private final HashMap<Base, Set<Location>> baseBlocks = new HashMap<>();
    private final HashMap<Location, Map.Entry<Base, BlockData>> blocks = new HashMap<>();
    private final HashMap<World, HashSet<Long>> loadedChunks = new HashMap<>();
    private final Gson gson = new Gson();

    public ChunkBasedProvider(BiMap<String, Base> mapping) {
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
    public Base instanceAt(Location loc) {
        return blocks.get(loc).getKey();
    }

    @Override
    public void setInstanceAt(Location loc, Base instance) {
        Base baseOrg = instanceAt(loc);
        baseBlocks.get(baseOrg).remove(loc);
        blocks.put(loc, new AbstractMap.SimpleEntry<>(instance, null));
        baseBlocks.get(instance).add(loc);
    }

    @Override
    public boolean hasBlock(Location block) {
        return blocks.containsKey(block);
    }

    @Override
    public void addBlock(Location block, Base instance, BlockData data) {
        if (!baseBlocks.containsKey(instance))
            baseBlocks.put(instance, new HashSet<>());
        baseBlocks.get(instance).add(block);
        blocks.put(block, new AbstractMap.SimpleEntry<>(instance, data));
    }

    @Override
    public void removeBlock(Location block) {
        Base base = instanceAt(block);
        baseBlocks.get(base).remove(block);
        blocks.remove(block);
    }

    @Override
    public void loadChunk(Chunk chunk) {
        Block dataBlock = chunk.getBlock(0, 0, 0);
        if (dataBlock.getType() != Material.JUKEBOX)
            return;

        ItemStack dataItem = ((Jukebox) dataBlock.getState()).getRecord();
        NBTValue value = NBTUtil.getTagValue(dataItem, "data");
        if (value == null)
            return;
        List<BlockDescription> blockList = gson.fromJson(value.asString(), new TypeToken<List<BlockDescription>>() {
        }.getType());
        for (BlockDescription desc : blockList)
            addBlock(new Location(Bukkit.getWorld(desc.worldName), desc.x, desc.y, desc.z),
                     mapping.get(desc.id),
                     desc.data);

        dataBlock.setType(Material.BEDROCK);
        if (!loadedChunks.containsKey(chunk.getWorld()))
            loadedChunks.put(chunk.getWorld(), new HashSet<>());
        loadedChunks.get(chunk.getWorld()).add(compress(chunk.getX(), chunk.getZ()));
    }

    @Override
    public void unloadChunk(Chunk chunk) {
        long chunkDesc = compress(chunk.getX(), chunk.getZ());
        if (loadedChunks.containsKey(chunk.getWorld()) && loadedChunks.get(chunk.getWorld()).contains(chunkDesc)) {
            LinkedList<BlockDescription> descList = new LinkedList<>();
            blocks.keySet().stream().filter(loc -> chunkDescAt(loc) == chunkDesc).forEach(loc -> {
                descList.add(new BlockDescription(loc,
                                                  mapping.inverse().get(blocks.get(loc).getKey()),
                                                  blocks.get(loc).getValue()));
                removeBlock(loc);
            });

            Block dataBlock = chunk.getBlock(0, 0, 0);
            dataBlock.setType(Material.JUKEBOX);
            ((Jukebox) dataBlock.getState()).setRecord(NBTUtil.setTagValue(new ItemStack(Material.PAPER),
                                                                           "data",
                                                                           NBTValue.of(gson.toJson(descList))));
            loadedChunks.get(chunk.getWorld()).remove(chunkDesc);
        }
    }

    @Override
    public void saveAll() {
        for (Map.Entry<World, HashSet<Long>> entry : loadedChunks.entrySet())
            for (long chunkDesc : entry.getValue()) {
                Map.Entry<Integer, Integer> decompressed = decompress(chunkDesc);
                unloadChunk(entry.getKey().getChunkAt(decompressed.getKey(), decompressed.getValue()));
            }
    }

    @Override
    public Set<Location> blocks() {
        return Collections.unmodifiableSet(blocks.keySet());
    }

    @Override
    public Set<Location> blocksOf(Base base) {
        return Collections.unmodifiableSet(baseBlocks.get(base));
    }

    private static class BlockDescription {
        String worldName;
        int x, y, z;
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
