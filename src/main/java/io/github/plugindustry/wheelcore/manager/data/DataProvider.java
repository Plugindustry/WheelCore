package io.github.plugindustry.wheelcore.manager.data;

import com.google.common.collect.BiMap;
import io.github.plugindustry.wheelcore.interfaces.Base;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Set;

public interface DataProvider {
    static DataProvider defaultProvider(BiMap<String, Base> mapping) {
        return new ChunkBasedProvider(mapping);
    }

    void loadChunk(Chunk chunk);

    void unloadChunk(Chunk chunk);

    Set<Location> blocks();

    Set<Location> blocksOf(Base base);

    BlockData dataAt(Location loc);

    void setDataAt(Location loc, BlockData data);

    Base instanceAt(Location loc);

    // This method should erase the block data of the modified block as well.
    void setInstanceAt(Location loc, Base instance);

    boolean hasBlock(Location block);

    void addBlock(Location block, Base instance, BlockData data);

    void removeBlock(Location block);

    void saveAll();
}
