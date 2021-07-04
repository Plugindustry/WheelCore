package io.github.plugindustry.wheelcore.manager.data;

import com.google.common.collect.BiMap;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import org.bukkit.Chunk;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface DataProvider {
    @Nonnull
    // TODO: Provider switch according to the config
    static DataProvider defaultProvider(BiMap<String, BlockBase> mapping) {
        return new ChunkBasedProvider(mapping);
    }

    void loadChunk(@Nonnull Chunk chunk);

    void unloadChunk(@Nonnull Chunk chunk);

    /**
     * @return A set containing all custom blocks loaded
     */
    @Nonnull
    Set<Location> blocks();

    /**
     * @param base The base instance of the blocks needed
     * @return A set containing all custom blocks which are loaded and with the given instance
     */
    @Nonnull
    Set<Location> blocksOf(@Nonnull BlockBase base);

    /**
     * @return The block data at the given location (or null if there isn't a custom block)
     */
    @Nullable
    BlockData dataAt(@Nonnull Location loc);

    /**
     * Set the block data at the given location to the given value (or do nothing if there isn't a custom block)
     */
    void setDataAt(@Nonnull Location loc, @Nullable BlockData data);

    @Nullable
    BlockBase instanceAt(@Nonnull Location loc);

    boolean hasBlock(@Nonnull Location block);

    void addBlock(@Nonnull Location block, @Nonnull BlockBase instance, @Nullable BlockData data);

    void removeBlock(@Nonnull Location block);

    /**
     * Save all blocks
     */
    void saveAll();
}
