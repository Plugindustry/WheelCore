package com.industrialworld.manager.data;

import com.google.common.collect.BiMap;
import com.industrialworld.interfaces.Base;
import com.industrialworld.interfaces.block.BlockData;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.util.Set;

public class FileBasedProvider implements DataProvider {
    private final BiMap<String, Base> mapping;
    private final File file;

    public FileBasedProvider(BiMap<String, Base> mapping, File file) {
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
    public Set<Location> blocksOf(Base base) {
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
    public Base instanceAt(Location loc) {
        return null;
    }

    @Override
    public void setInstanceAt(Location loc, Base instance) {

    }

    @Override
    public boolean hasBlock(Location block) {
        return false;
    }

    @Override
    public void addBlock(Location block, Base instance, BlockData data) {

    }

    @Override
    public void removeBlock(Location block) {

    }

    @Override
    public void saveAll() {

    }
}
