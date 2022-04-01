package io.github.plugindustry.wheelcore.manager.data.block;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.CollectionUtil;
import io.github.plugindustry.wheelcore.utils.GsonHelper;
import io.github.plugindustry.wheelcore.utils.Pair;
import org.bukkit.*;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ChunkBasedProvider implements BlockDataProvider {
    private static final Gson gson;
    private final static NamespacedKey CHUNK_DATA_KEY = new NamespacedKey(WheelCore.instance, "chunk_data");

    static {
        GsonBuilder gbs = GsonHelper.bukkitCompat();
        gbs.registerTypeAdapter(BlockData.class, GsonHelper.POLYMORPHISM_SERIALIZER);
        gbs.registerTypeAdapter(BlockData.class, GsonHelper.POLYMORPHISM_DESERIALIZER);
        gson = gbs.create();
    }

    private final HashMap<BlockBase, Set<Location>> baseBlocks = new HashMap<>();
    private final HashMap<Location, Pair<BlockBase, BlockData>> blocks = new HashMap<>();
    private final HashMap<World, HashMap<Long, HashSet<Location>>> blockInChunks = new HashMap<>();

    private static long chunkDescAt(Location loc) {
        return compress(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    private static long compress(int i1, int i2) {
        return ((i1 & 0x00000000ffffffffL) << 32) | (i2 & 0x00000000ffffffffL);
    }

    @Override
    public BlockData dataAt(@Nonnull Location loc) {
        loc = loc.clone();
        loc.setPitch(0);
        loc.setYaw(0);

        if (!hasBlock(loc)) return null;

        return blocks.get(loc).second;
    }

    @Override
    public void setDataAt(@Nonnull Location loc, BlockData data) {
        loc = loc.clone();
        loc.setPitch(0);
        loc.setYaw(0);

        if (!hasBlock(loc)) return;

        blocks.get(loc).second = data;
    }

    @Override
    @Nullable
    public BlockBase instanceAt(@Nonnull Location loc) {
        loc = loc.clone();
        loc.setPitch(0);
        loc.setYaw(0);

        return blocks.containsKey(loc) ? blocks.get(loc).first : null;
    }

    @Override
    public boolean hasBlock(@Nonnull Location block) {
        block = block.clone();
        block.setPitch(0);
        block.setYaw(0);

        return blocks.containsKey(block);
    }

    @Override
    public void addBlock(@Nonnull Location block, @Nonnull BlockBase instance, BlockData data) {
        block = block.clone();
        block.setPitch(0);
        block.setYaw(0);

        baseBlocks.putIfAbsent(instance, new HashSet<>());
        baseBlocks.get(instance).add(block);
        blocks.put(block, Pair.of(instance, data));
        blockInChunks.putIfAbsent(block.getWorld(), new HashMap<>());
        blockInChunks.get(block.getWorld()).putIfAbsent(chunkDescAt(block), new HashSet<>());
        blockInChunks.get(block.getWorld()).get(chunkDescAt(block)).add(block);
    }

    @Override
    public void removeBlock(@Nonnull Location block) {
        block = block.clone();
        block.setPitch(0);
        block.setYaw(0);

        BlockBase base = instanceAt(block);
        if (baseBlocks.containsKey(base)) baseBlocks.get(base).remove(block);
        if (blockInChunks.containsKey(block.getWorld()) && blockInChunks.get(block.getWorld())
                                                                        .containsKey(chunkDescAt(block)))
            blockInChunks.get(block.getWorld()).get(chunkDescAt(block)).remove(block);
        blocks.remove(block);
    }

    @Override
    public void loadChunk(@Nonnull Chunk chunk) {
        //DebuggingLogger.debug("Load chunk " + chunk.getX() + " " + chunk.getZ());
        World world = chunk.getWorld();
        List<BlockDescription> blockList = gson.fromJson(chunk.getPersistentDataContainer()
                                                              .getOrDefault(CHUNK_DATA_KEY,
                                                                      PersistentDataType.STRING, "[]"),
                new TypeToken<List<BlockDescription>>() {
                }.getType());
        for (BlockDescription desc : blockList)
            if (MainManager.getBlockInstanceFromId(desc.id) != null)
                addBlock(new Location(world, desc.x, desc.y, desc.z),
                        Objects.requireNonNull(MainManager.getBlockInstanceFromId(desc.id)), desc.data);
    }

    @Override
    public void unloadChunk(@Nonnull Chunk chunk) {
        //DebuggingLogger.debug("Unload chunk " + chunk.getX() + " " + chunk.getZ());
        saveChunkImpl(chunk, true);
    }

    @SuppressWarnings("unchecked")
    private void saveChunkImpl(Chunk chunk, boolean remove) {
        long chunkDesc = compress(chunk.getX(), chunk.getZ());

        if (!blockInChunks.containsKey(chunk.getWorld())) return;
        HashSet<Location> locations = blockInChunks.get(chunk.getWorld()).get(chunkDesc);
        if (locations != null) {
            LinkedList<BlockDescription> descriptions = new LinkedList<>();
            locations.forEach(loc -> descriptions.add(
                    new BlockDescription(loc, MainManager.getIdFromInstance(blocks.get(loc).first),
                            blocks.get(loc).second)));
            if (remove) {
                ((HashSet<Location>) locations.clone()).forEach(this::removeBlock);
                blockInChunks.get(chunk.getWorld()).remove(chunkDesc);
            }

            chunk.getPersistentDataContainer()
                 .set(CHUNK_DATA_KEY, PersistentDataType.STRING, gson.toJson(descriptions));
        }
    }

    @Override
    public void beforeSave() {
        for (World world : Bukkit.getWorlds()) {
            Chunk[] loaded = world.getLoadedChunks();
            for (Chunk chunk : loaded)
                saveChunkImpl(chunk, false);
        }
    }

    @Override
    public void afterSave() {
    }

    @Nonnull
    @Override
    public Set<Location> blocks() {
        return CollectionUtil.unmodifiableCopyOnReadSet(blocks.keySet(), Location::clone);
    }

    @Nonnull
    @Override
    public Set<Location> blockInChunk(@Nonnull Chunk chunk) {
        if (blockInChunks.containsKey(chunk.getWorld()))
            if (blockInChunks.get(chunk.getWorld()).containsKey(compress(chunk.getX(), chunk.getZ())))
                return CollectionUtil.unmodifiableCopyOnReadSet(
                        blockInChunks.get(chunk.getWorld()).get(compress(chunk.getX(), chunk.getZ())),
                        Location::clone);
            else return Collections.emptySet();
        else return Collections.emptySet();
    }

    @Nonnull
    @Override
    public Set<Location> blocksOf(@Nonnull BlockBase base) {
        return baseBlocks.containsKey(base) ?
                CollectionUtil.unmodifiableCopyOnReadSet(baseBlocks.get(base), Location::clone) :
                Collections.emptySet();
    }

    private static class BlockDescription {
        int x;
        int y;
        int z;
        String id;
        BlockData data;

        BlockDescription() {
        }

        BlockDescription(Location loc, String id, BlockData data) {
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
            this.id = id;
            this.data = data;
        }
    }
}
