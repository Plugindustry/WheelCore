package io.github.plugindustry.wheelcore.manager.data;

import com.google.common.collect.BiMap;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public void loadChunk(@Nonnull Chunk chunk) {

    }

    @Override
    public void unloadChunk(@Nonnull Chunk chunk) {

    }

    @Nonnull
    @Override
    public Set<Location> blocks() {
        return null;
    }

    @Nonnull
    @Override
    public Set<Location> blocksOf(@Nonnull BlockBase base) {
        return null;
    }

    @Override
    public BlockData dataAt(@Nonnull Location loc) {
        return null;
    }

    @Override
    public void setDataAt(@Nonnull Location loc, BlockData data) {

    }

    @Override
    public @Nullable
    BlockBase instanceAt(@Nonnull Location loc) {
        return null;
    }

    @Override
    public boolean hasBlock(@Nonnull Location block) {
        return false;
    }

    @Override
    public void addBlock(@Nonnull Location block, @Nonnull BlockBase instance, BlockData data) {

    }

    @Override
    public void removeBlock(@Nonnull Location block) {

    }

    @Override
    public void saveAll() {
        for (World world : Bukkit.getWorlds())
            world.save();
    }
}
