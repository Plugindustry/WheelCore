package io.github.plugindustry.wheelcore.manager.data.block;

import com.google.common.collect.BiMap;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import org.bukkit.Chunk;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.Set;

// TODO: Implement this
public class FileBasedProvider implements BlockDataProvider {
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
        return Collections.emptySet();
    }

    @Nonnull
    @Override
    public Set<Location> blocksOf(@Nonnull BlockBase base) {
        return Collections.emptySet();
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
    public void beforeSave() {

    }

    @Override
    public void afterSave() {

    }
}
