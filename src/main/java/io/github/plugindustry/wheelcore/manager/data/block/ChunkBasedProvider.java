package io.github.plugindustry.wheelcore.manager.data.block;

import com.google.gson.*;
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
import java.util.concurrent.ConcurrentHashMap;

public class ChunkBasedProvider implements BlockDataProvider {
    public static final JsonSerializer<BlockDescription> BLOCK_DESC_SERIALIZER = (obj, type, jsonSerializationContext) -> {
        JsonObject result = new JsonObject();
        result.addProperty("x", obj.x);
        result.addProperty("y", obj.y);
        result.addProperty("z", obj.z);
        result.add("id", jsonSerializationContext.serialize(obj.id));
        result.add("data", jsonSerializationContext.serialize(obj.data));
        return result;
    };
    public static final JsonDeserializer<BlockDescription> BLOCK_DESC_DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        BlockDescription desc = new BlockDescription();
        desc.x = jsonObject.get("x").getAsInt();
        desc.y = jsonObject.get("y").getAsInt();
        desc.z = jsonObject.get("z").getAsInt();
        desc.id = jsonDeserializationContext.deserialize(jsonObject.get("id"), NamespacedKey.class);
        desc.data = jsonDeserializationContext.deserialize(jsonObject.get("data"),
                new TypeToken<Map<NamespacedKey, BlockData>>() {
                }.getType());
        return desc;
    };
    private static final Gson gson;
    private final static NamespacedKey CHUNK_DATA_KEY = new NamespacedKey(WheelCore.getInstance(), "chunk_data");

    static {
        GsonBuilder gbs = GsonHelper.bukkitCompat();
        gbs.registerTypeAdapter(BlockData.class, GsonHelper.POLYMORPHISM_SERIALIZER);
        gbs.registerTypeAdapter(BlockData.class, GsonHelper.POLYMORPHISM_DESERIALIZER);
        gbs.registerTypeAdapter(BlockDescription.class, BLOCK_DESC_SERIALIZER);
        gbs.registerTypeAdapter(BlockDescription.class, BLOCK_DESC_DESERIALIZER);
        gson = gbs.create();
    }

    private final ConcurrentHashMap<BlockBase, Set<Location>> baseBlocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Location, Pair<BlockBase, Map<NamespacedKey, BlockData>>> blocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<World, HashMap<Long, HashSet<Location>>> blockInChunks = new ConcurrentHashMap<>();

    private static long chunkDescAt(Location loc) {
        return compress(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    private static long compress(int i1, int i2) {
        return ((i1 & 0x00000000ffffffffL) << 32) | (i2 & 0x00000000ffffffffL);
    }

    private void ensureLoaded(@Nonnull Location loc) {
        ensureLoaded(loc.getChunk());
    }

    private void ensureLoaded(@Nonnull Chunk chunk) {
        if (!chunk.isLoaded()) chunk.load(false);
    }

    @Override
    public BlockData dataAt(@Nonnull Location loc) {
        loc = loc.clone();
        loc.setPitch(0);
        loc.setYaw(0);

        if (!hasBlock(loc)) return null;

        return blocks.get(loc).second.get(null);
    }

    @Override
    public void setDataAt(@Nonnull Location loc, @Nullable BlockData data) {
        loc = loc.clone();
        loc.setPitch(0);
        loc.setYaw(0);

        if (!hasBlock(loc)) return;

        Pair<BlockBase, Map<NamespacedKey, BlockData>> pair = blocks.get(loc);
        if (pair.second.containsKey(null)) pair.second.get(null).unload();

        if (data == null) pair.second.remove(null);
        else pair.second.put(null, data);
    }

    @Nullable
    @Override
    public BlockData additionalDataAt(@Nonnull Location loc, @Nonnull NamespacedKey key) {
        Objects.requireNonNull(key);

        loc = loc.clone();
        loc.setPitch(0);
        loc.setYaw(0);

        ensureLoaded(loc);
        if (!blocks.containsKey(loc)) return null;

        return blocks.get(loc).second.get(key);
    }

    @Override
    public void setAdditionalDataAt(@Nonnull Location loc, @Nonnull NamespacedKey key, @Nullable BlockData data) {
        Objects.requireNonNull(key);

        loc = loc.clone();
        loc.setPitch(0);
        loc.setYaw(0);

        ensureLoaded(loc);
        if (!blocks.containsKey(loc)) blocks.put(loc, Pair.of(null, new HashMap<>()));

        Pair<BlockBase, Map<NamespacedKey, BlockData>> pair = blocks.get(loc);
        if (pair.second.containsKey(key)) pair.second.get(key).unload();

        if (data == null) pair.second.remove(key);
        else pair.second.put(key, data);
    }

    @Override
    @Nullable
    public BlockBase instanceAt(@Nonnull Location loc) {
        loc = loc.clone();
        loc.setPitch(0);
        loc.setYaw(0);

        return hasBlock(loc) ? blocks.get(loc).first : null;
    }

    @Override
    public boolean hasBlock(@Nonnull Location block) {
        block = block.clone();
        block.setPitch(0);
        block.setYaw(0);

        ensureLoaded(block);
        return blocks.containsKey(block) && blocks.get(block).first != null;
    }

    @Override
    public void addBlock(@Nonnull Location block, @Nonnull BlockBase instance, @Nullable BlockData data) {
        block = block.clone();
        block.setPitch(0);
        block.setYaw(0);

        ensureLoaded(block);
        if (!baseBlocks.containsKey(instance)) baseBlocks.put(instance, new HashSet<>());
        baseBlocks.get(instance).add(block);
        Map<NamespacedKey, BlockData> map = blocks.containsKey(block) ? blocks.get(block).second : new HashMap<>();
        if (!(data == null)) map.put(null, data);
        blocks.put(block, Pair.of(instance, map));
        World world = Objects.requireNonNull(block.getWorld());
        HashMap<Long, HashSet<Location>> worldMap;
        if (!blockInChunks.containsKey(world)) blockInChunks.put(world, worldMap = new HashMap<>());
        else worldMap = blockInChunks.get(world);
        long chunkDesc = chunkDescAt(block);
        if (!worldMap.containsKey(chunkDesc)) worldMap.put(chunkDesc, new HashSet<>());
        worldMap.get(chunkDesc).add(block);
    }

    @Override
    public void removeBlock(@Nonnull Location block) {
        block = block.clone();
        block.setPitch(0);
        block.setYaw(0);

        ensureLoaded(block);
        BlockBase base = instanceAt(block);
        if (baseBlocks.containsKey(base)) baseBlocks.get(base).remove(block);
        if (blockInChunks.containsKey(block.getWorld()) &&
            blockInChunks.get(block.getWorld()).containsKey(chunkDescAt(block)))
            blockInChunks.get(block.getWorld()).get(chunkDescAt(block)).remove(block);
        Pair<BlockBase, Map<NamespacedKey, BlockData>> pair = blocks.get(block);
        pair.second.values().forEach(BlockData::unload);
        blocks.remove(block);
    }

    @Override
    public void loadChunk(@Nonnull Chunk chunk) {
        World world = chunk.getWorld();
        List<BlockDescription> blockList = gson.fromJson(
                chunk.getPersistentDataContainer().getOrDefault(CHUNK_DATA_KEY, PersistentDataType.STRING, "[]"),
                new TypeToken<List<BlockDescription>>() {
                }.getType());
        for (BlockDescription desc : blockList)
            if (MainManager.getBlockInstanceFromId(desc.id) != null) {
                Location loc = new Location(world, desc.x, desc.y, desc.z);
                addBlock(loc, Objects.requireNonNull(MainManager.getBlockInstanceFromId(desc.id)), desc.data.get(null));
                desc.data.forEach((k, v) -> {
                    if (k == null) return;
                    setAdditionalDataAt(loc, k, v);
                });
            }
    }

    @Override
    public void unloadChunk(@Nonnull Chunk chunk) {
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
    public Set<Location> blocksInChunk(@Nonnull Chunk chunk) {
        ensureLoaded(chunk);
        if (blockInChunks.containsKey(chunk.getWorld()))
            if (blockInChunks.get(chunk.getWorld()).containsKey(compress(chunk.getX(), chunk.getZ())))
                return CollectionUtil.unmodifiableCopyOnReadSet(
                        blockInChunks.get(chunk.getWorld()).get(compress(chunk.getX(), chunk.getZ())), Location::clone);
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
        public int x;
        public int y;
        public int z;
        public NamespacedKey id;
        public Map<NamespacedKey, BlockData> data;

        public BlockDescription() {
        }

        public BlockDescription(Location loc, NamespacedKey id, Map<NamespacedKey, BlockData> data) {
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
            this.id = id;
            this.data = data;
        }
    }
}