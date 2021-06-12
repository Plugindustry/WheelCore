package io.github.plugindustry.wheelcore.manager.data;

import com.google.common.collect.BiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
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

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkBasedProvider implements DataProvider {
    private static final Gson gson;
    private static final TypeAdapter<BlockDescription> BLOCK_DESCRIPTION_TYPE_ADAPTER = new TypeAdapter<BlockDescription>() {
        @Override
        public void write(JsonWriter out, BlockDescription value) throws IOException {
            out.beginObject();
            out.name("x").value(value.x);
            out.name("y").value(value.y);
            out.name("z").value(value.z);
            out.name("id").value(value.id);
            if (value.data != null) {
                out.name("dataType").value(value.data.getClass().getName());
                out.name("data").value(gson.toJson(value.data));
            }
            out.endObject();
        }

        @Override
        public BlockDescription read(JsonReader in) throws IOException {
            int x = 0;
            int y = 0;
            int z = 0;
            String id = "";
            String dataType = null;
            String data = "";
            in.beginObject();
            while (in.hasNext()) {
                switch (in.nextName()) {
                    case "x":
                        x = in.nextInt();
                        break;
                    case "y":
                        y = in.nextInt();
                        break;
                    case "z":
                        z = in.nextInt();
                        break;
                    case "id":
                        id = in.nextString();
                        break;
                    case "dataType":
                        dataType = in.nextString();
                        break;
                    case "data":
                        data = in.nextString();
                        break;
                    default:
                        break;
                }
            }
            in.endObject();

            Class<?> dataClass = null;
            if (dataType != null) {
                try {
                    dataClass = Class.forName(dataType);
                    if (!BlockData.class.isAssignableFrom(dataClass))
                        dataClass = null;
                } catch (ClassNotFoundException ignored) {
                }
            }

            return new BlockDescription(new Location(null, x, y, z),
                                        id,
                                        dataClass == null ? null : (BlockData) gson.fromJson(data, dataClass));
        }
    };
    private final BiMap<String, BlockBase> mapping;
    private final HashMap<BlockBase, Set<Location>> baseBlocks = new HashMap<>();
    private final HashMap<Location, Map.Entry<BlockBase, BlockData>> blocks = new HashMap<>();
    private final HashMap<World, HashMap<Long, HashSet<Location>>> blockInChunks = new HashMap<>();

    static {
        GsonBuilder gbs = new GsonBuilder();
        gbs.registerTypeAdapter(BlockDescription.class, BLOCK_DESCRIPTION_TYPE_ADAPTER);
        gson = gbs.create();
    }

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
        baseBlocks.putIfAbsent(instance, new HashSet<>());
        baseBlocks.get(instance).add(block);
        blocks.put(block, new AbstractMap.SimpleEntry<>(instance, data));
        blockInChunks.putIfAbsent(block.getWorld(), new HashMap<>());
        blockInChunks.get(block.getWorld()).putIfAbsent(chunkDescAt(block), new HashSet<>());
        blockInChunks.get(block.getWorld()).get(chunkDescAt(block)).add(block);
    }

    @Override
    public void removeBlock(Location block) {
        BlockBase base = instanceAt(block);
        if (baseBlocks.containsKey(base))
            baseBlocks.get(base).remove(block);
        if (blockInChunks.containsKey(block.getWorld()) && blockInChunks.get(block.getWorld()).containsKey(chunkDescAt(
                block)))
            blockInChunks.get(block.getWorld()).get(chunkDescAt(block)).remove(block);
        blocks.remove(block);
    }

    @Override
    public void loadChunk(Chunk chunk) {
        //DebuggingLogger.debug("Load chunk " + chunk.getX() + " " + chunk.getZ());
        World world = chunk.getWorld();

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
            addBlock(new Location(world, desc.x, desc.y, desc.z), mapping.get(desc.id), desc.data);

        dataBlock.setType(Material.BEDROCK);
    }

    @Override
    public void unloadChunk(Chunk chunk) {
        //DebuggingLogger.debug("Unload chunk " + chunk.getX() + " " + chunk.getZ());
        saveChunkImpl(chunk, true);
    }

    @SuppressWarnings("unchecked")
    private void saveChunkImpl(Chunk chunk, boolean remove) {
        long chunkDesc = compress(chunk.getX(), chunk.getZ());

        if (!blockInChunks.containsKey(chunk.getWorld()))
            return;
        HashSet<Location> locations = blockInChunks.get(chunk.getWorld()).get(chunkDesc);
        if (locations != null) {
            LinkedList<BlockDescription> descriptions = new LinkedList<>();
            locations.forEach(loc -> descriptions.add(new BlockDescription(loc,
                                                                           mapping.inverse()
                                                                                   .get(blocks.get(loc).getKey()),
                                                                           blocks.get(loc).getValue())));
            if (remove) {
                ((HashSet<Location>) locations.clone()).forEach(this::removeBlock);
                blockInChunks.get(chunk.getWorld()).remove(chunkDesc);
            }

            Block dataBlock = chunk.getBlock(0, 0, 0);
            dataBlock.setType(Material.JUKEBOX);
            String json = gson.toJson(descriptions);
            DebuggingLogger.debug(json);

            Jukebox bs = (Jukebox) dataBlock.getState();
            bs.setRecord(NBTUtil.setTagValue(new ItemStack(Material.MUSIC_DISC_11), "data", NBTValue.of(json)));
            bs.update();
        }
    }

    @Override
    public void saveAll() {
        for (World world : Bukkit.getWorlds()) {
            Chunk[] loaded = world.getLoadedChunks();
            for (Chunk chunk : loaded)
                saveChunkImpl(chunk, false);
            world.save();
            for (Chunk chunk : loaded)
                chunk.getBlock(0, 0, 0).setType(Material.BEDROCK);
        }
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
        int x;
        int y;
        int z;
        String id;
        BlockData data;

        BlockDescription(Location loc, String id, BlockData data) {
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
            this.id = id;
            this.data = data;
        }
    }
}
