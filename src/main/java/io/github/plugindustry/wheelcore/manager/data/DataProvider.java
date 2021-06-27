package io.github.plugindustry.wheelcore.manager.data;

import com.google.common.collect.BiMap;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import org.bukkit.Chunk;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Set;

public interface DataProvider {
    static DataProvider defaultProvider(BiMap<String, BlockBase> mapping) {
        return new ChunkBasedProvider(mapping);
    }

    void loadChunk(Chunk chunk);

    void unloadChunk(Chunk chunk);

    @Nonnull
    Set<Location> blocks();

    @Nonnull
    Set<Location> blocksOf(BlockBase base);

    BlockData dataAt(Location loc);

    void setDataAt(Location loc, BlockData data);

    BlockBase instanceAt(Location loc);

    boolean hasBlock(Location block);

    void addBlock(Location block, BlockBase instance, BlockData data);

    void removeBlock(Location block);

    void saveAll();
}
