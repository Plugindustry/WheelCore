package io.github.plugindustry.wheelcore.manager.data.item;

import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.interfaces.item.ItemData;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface ItemDataProvider {
    @Nonnull
    // TODO: Provider switching according to the config
    static ItemDataProvider defaultProvider() {
        return new PersistenceBasedProvider();
    }

    /**
     * @return The corresponding ItemBase instance of the given item, or null if it is not a custom item
     */
    @Nullable
    ItemBase getInstance(@Nullable ItemStack itemStack);

    /**
     * @return The item data of the given item, or null if there is no item data
     */
    @Nullable
    ItemData getData(@Nullable ItemStack itemStack);

    /**
     * @param key The key of desired additional data
     * @return The additional data with the given key of the given item (or null if the additional data doesn't exist)
     */
    @Nullable
    ItemData getAdditionalData(@Nullable ItemStack itemStack, @Nonnull NamespacedKey key);

    /**
     * @return Ore dictionary entries of the given item
     */
    @Nonnull
    Set<String> getOreDictionary(@Nullable ItemStack itemStack);

    /**
     * Set the corresponding ItemBase instance of the given item to the given value.
     */
    void setInstance(@Nonnull ItemStack itemStack, @Nullable ItemBase instance);

    /**
     * Set the item data of the given item to the given value.
     */
    void setData(@Nonnull ItemStack itemStack, @Nullable ItemData data);

    /**
     * @param key The key of the additional data
     *            Set the additional data with the given key of the given item to the given value.
     */
    void setAdditionalData(@Nonnull ItemStack itemStack, @Nonnull NamespacedKey key, @Nullable ItemData data);

    /**
     * Set the ore dictionary entries of the given item to the given value.
     */
    void setOreDictionary(@Nonnull ItemStack itemStack, @Nonnull Set<String> oreDictionary);
}