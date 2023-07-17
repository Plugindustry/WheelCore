package io.github.plugindustry.wheelcore.manager.data.block;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import io.github.plugindustry.wheelcore.interfaces.block.Destroyable;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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

    /**
     * Load a chunk.
     * For internal use only.
     */
    void loadChunk(@Nonnull Chunk chunk);

    /**
     * Unload a chunk.
     * For internal use only.
     */
    void unloadChunk(@Nonnull Chunk chunk);

    /**
     * @return A set containing all custom blocks loaded
     * Note that the returned set may be based on the internal container, so any modifications (adding/removing blocks etc.) mustn't be done when traversing this set.
     * You can delay modifications by using {@link io.github.plugindustry.wheelcore.manager.MainManager#queuePostTickTask(Runnable)}.
     */
    @Nonnull
    Set<Location> blocks();

    /**
     * @param chunk The chunk of the blocks needed
     * @return A set containing all custom blocks loaded that belong to the given chunk
     * Note that the returned set may be based on the internal container, so any modifications (adding/removing blocks etc.) mustn't be done when traversing this set.
     * You can delay modifications by using {@link io.github.plugindustry.wheelcore.manager.MainManager#queuePostTickTask(Runnable)}.
     */
    @Nonnull
    Set<Location> blocksInChunk(@Nonnull Chunk chunk);

    /**
     * @param base The base instance of the blocks needed
     * @return A set containing all custom blocks which are loaded and with the given instance
     * Note that the returned set may be based on the internal container, so any modifications (adding/removing blocks etc.) mustn't be done when traversing this set.
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
     * Set the block data at the given location to the given value (or do nothing if there isn't a custom block).
     */
    void setDataAt(@Nonnull Location loc, @Nullable BlockData data);

    /**
     * @param key The key of desired additional data
     * @return The additional data with the given key at the given location (or null if the additional data doesn't exist)
     */
    @Nullable
    BlockData additionalDataAt(@Nonnull Location loc, @Nonnull NamespacedKey key);

    /**
     * @param key The key of the additional data
     *            Set the additional data with the given key at the given location to the given value (the block is not required to be a custom block).
     */
    void setAdditionalDataAt(@Nonnull Location loc, @Nonnull NamespacedKey key, @Nullable BlockData data);

    /**
     * @return The corresponding BlockBase instance at the given location, or null if there is no custom block
     */
    @Nullable
    BlockBase instanceAt(@Nonnull Location loc);

    /**
     * @return Whether the block is a custom block
     */
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
     * Invoked before saving all.
     */
    void beforeSave();

    /**
     * Invoked after saving all.
     */
    void afterSave();
}