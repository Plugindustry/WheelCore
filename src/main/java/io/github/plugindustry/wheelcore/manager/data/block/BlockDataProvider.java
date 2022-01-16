package io.github.plugindustry.wheelcore.manager.data.block;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import io.github.plugindustry.wheelcore.interfaces.block.Destroyable;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface BlockDataProvider {
    @Nonnull
    // TODO: Provider switching according to the config
    static BlockDataProvider defaultProvider() {
        return new ChunkBasedProvider();
    }

    void loadChunk(@Nonnull Chunk chunk);

    void unloadChunk(@Nonnull Chunk chunk);

    /**
     * @return A set containing all custom blocks loaded.
     * Note that the returned set may be based on the internal container, so any modifications mustn't be done when traversing this set.
     * You can delay modifications by using {@link io.github.plugindustry.wheelcore.manager.MainManager#queuePostTickTask(Runnable)}.
     */
    @Nonnull
    Set<Location> blocks();

    /**
     * @param base The base instance of the blocks needed
     * @return A set containing all custom blocks which are loaded and with the given instance.
     * Note that the returned set may be based on the internal container, so any modifications mustn't be done when traversing this set.
     * You can delay modifications by using {@link io.github.plugindustry.wheelcore.manager.MainManager#queuePostTickTask(Runnable)}.
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

    /**
     * Don't use this method unless you know exactly what you are doing.
     * To place a block, use {@link io.github.plugindustry.wheelcore.interfaces.block.Placeable#onBlockPlace(ItemStack, Block, Block, Player)} instead
     */
    void addBlock(@Nonnull Location block, @Nonnull BlockBase instance, @Nullable BlockData data);

    /**
     * This method is only used to simply remove a block without doing anything else.
     * To simulate destroying a block, use {@link io.github.plugindustry.wheelcore.interfaces.block.Destroyable#onBlockDestroy(Block, Destroyable.DestroyMethod, ItemStack, Player)} instead
     */
    void removeBlock(@Nonnull Location block);

    /**
     * Before save all
     */
    void beforeSave();

    /**
     * After save all
     */
    void afterSave();
}
