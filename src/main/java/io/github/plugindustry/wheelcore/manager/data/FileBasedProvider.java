package io.github.plugindustry.wheelcore.manager.data;

import com.google.common.collect.BiMap;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.Set;

// TODO: Implement this
public class FileBasedProvider implements DataProvider {
    private final BiMap<String, BlockBase> mapping;
    private final File file;

    public FileBasedProvider(BiMap<String, BlockBase> mapping, File file) {
        this.mapping = mapping;
        this.file = file;
    }

    @Override
    public void loadChunk(Chunk chunk) {

    }

    @Override
    public void unloadChunk(Chunk chunk) {

    }

    @Override
    public Set<Location> blocks() {
        return null;
    }

    @Override
    public Set<Location> blocksOf(BlockBase base) {
        return null;
    }

    @Override
    public BlockData dataAt(Location loc) {
        return null;
    }

    @Override
    public void setDataAt(Location loc, BlockData data) {

    }

    @Override
    public BlockBase instanceAt(Location loc) {
        return null;
    }

    @Override
    public boolean hasBlock(Location block) {
        return false;
    }

    @Override
    public void addBlock(Location block, BlockBase instance, BlockData data) {

    }

    @Override
    public void removeBlock(Location block) {

    }

    @Override
    public void saveAll() {
        for (World world : Bukkit.getWorlds())
            world.save();
    }
}
