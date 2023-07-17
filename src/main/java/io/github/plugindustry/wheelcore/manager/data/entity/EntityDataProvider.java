package io.github.plugindustry.wheelcore.manager.data.entity;

import io.github.plugindustry.wheelcore.interfaces.entity.EntityBase;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityData;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EntityDataProvider {
    @Nonnull
    // TODO: Provider switching according to the config
    static EntityDataProvider defaultProvider() {
        return new PersistenceBasedProvider();
    }

    /**
     * Load an entity.
     * For internal use only.
     */
    void loadEntity(@Nonnull Entity entity);

    /**
     * Unload an entity.
     * For internal use only.
     */
    void unloadEntity(@Nonnull Entity entity);

    /**
     * Invoked before saving all
     */
    void beforeSave();

    /**
     * Invoked after saving all
     */
    void afterSave();

    /**
     * @return The corresponding EntityBase instance of the given entity, or null if it is not a custom entity
     */
    @Nullable
    EntityBase instanceOf(@Nonnull Entity entity);

    /**
     * @return Whether the entity is a custom entity
     */
    boolean hasEntity(@Nonnull Entity entity);

    /**
     * @return The entity data of the given entity, or null if it is not a custom entity or there is no entity data
     */
    @Nullable
    EntityData getData(@Nonnull Entity entity);

    /**
     * Set the entity data of the given entity to the given value (or do nothing if it is not a custom entity).
     */
    void setData(@Nonnull Entity entity, @Nullable EntityData data);

    /**
     * @param key The key of desired additional data
     * @return The additional data with the given key of the given entity (or null if the additional data doesn't exist)
     */
    @Nullable
    EntityData getAdditionalData(@Nonnull Entity entity, @Nonnull NamespacedKey key);

    /**
     * @param key The key of the additional data
     *            Set the additional data with the given key of the given entity to the given value (the entity is not required to be a custom entity).
     */
    void setAdditionalData(@Nonnull Entity entity, @Nonnull NamespacedKey key, @Nullable EntityData data);

    /**
     * Don't use this method unless you know exactly what you are doing.
     * To place a block, use {@link io.github.plugindustry.wheelcore.interfaces.block.Placeable#onBlockPlace(ItemStack, Block, Block, Player)} instead
     */
    void addEntity(@Nonnull Entity entity, @Nonnull EntityBase instance, @Nullable EntityData data);
}