package io.github.plugindustry.wheelcore.manager.data.item;

import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.interfaces.item.ItemData;
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

    @Nullable
    ItemBase getInstance(@Nullable ItemStack itemStack);

    @Nullable
    ItemData getData(@Nullable ItemStack itemStack);

    @Nonnull
    Set<String> getOreDictionary(@Nullable ItemStack itemStack);

    void setInstance(@Nonnull ItemStack itemStack, @Nullable ItemBase instance);

    void setData(@Nonnull ItemStack itemStack, @Nullable ItemData data);

    void setOreDictionary(@Nonnull ItemStack itemStack, @Nonnull Set<String> oreDictionary);
}
