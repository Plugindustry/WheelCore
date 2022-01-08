package io.github.plugindustry.wheelcore.manager.data.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.interfaces.item.ItemData;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.GsonHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class PersistenceBasedProvider implements ItemDataProvider {
    private static final NamespacedKey ITEM_TYPE_KEY = new NamespacedKey(WheelCore.instance, "item_type");
    private static final NamespacedKey ITEM_DATA_KEY = new NamespacedKey(WheelCore.instance, "item_data");
    private static final NamespacedKey ITEM_ORE_DICTIONARY_KEY = new NamespacedKey(WheelCore.instance,
                                                                                   "item_ore_dictionary");
    private static final Gson gson;

    static {
        GsonBuilder gbs = GsonHelper.bukkitCompact();
        gbs.registerTypeAdapter(ItemData.class, GsonHelper.POLYMORPHISM_SERIALIZER);
        gbs.registerTypeAdapter(ItemData.class, GsonHelper.POLYMORPHISM_DESERIALIZER);
        gson = gbs.create();
    }

    @Nullable
    @Override
    public ItemBase getInstance(@Nonnull ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;
        return MainManager.getItemInstanceFromId(Objects.requireNonNull(itemStack.getItemMeta())
                                                         .getPersistentDataContainer()
                                                         .get(ITEM_TYPE_KEY, PersistentDataType.STRING));
    }

    @Nullable
    @Override
    public ItemData getData(@Nonnull ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return null;
        String data = Objects.requireNonNull(itemStack.getItemMeta()).getPersistentDataContainer().get(ITEM_DATA_KEY,
                                                                                                       PersistentDataType.STRING);
        return data == null ? null : gson.fromJson(data, ItemData.class);
    }

    @Nonnull
    @Override
    public Set<String> getOreDictionary(@Nonnull ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return Collections.emptySet();
        String oreDict = Objects.requireNonNull(itemStack.getItemMeta()).getPersistentDataContainer().get(
                ITEM_ORE_DICTIONARY_KEY,
                PersistentDataType.STRING);
        return oreDict == null ? Collections.emptySet() : gson.fromJson(oreDict, new TypeToken<Set<String>>() {
        }.getType());
    }

    @Override
    public void setInstance(@Nonnull ItemStack itemStack, @Nullable ItemBase instance) {
        if (instance == null) {
            if (itemStack.hasItemMeta()) {
                ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
                meta.getPersistentDataContainer().remove(ITEM_TYPE_KEY);
                itemStack.setItemMeta(meta);
            }
        } else {
            ItemMeta meta = itemStack.hasItemMeta() ?
                            Objects.requireNonNull(itemStack.getItemMeta()) :
                            Bukkit.getItemFactory().getItemMeta(itemStack.getType());
            Objects.requireNonNull(meta).getPersistentDataContainer().set(ITEM_TYPE_KEY,
                                                                          PersistentDataType.STRING,
                                                                          Objects.requireNonNull(MainManager.getIdFromInstance(
                                                                                  instance)));
            itemStack.setItemMeta(meta);
        }
    }

    @Override
    public void setData(@Nonnull ItemStack itemStack, @Nullable ItemData data) {
        if (data == null) {
            if (itemStack.hasItemMeta()) {
                ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
                meta.getPersistentDataContainer().remove(ITEM_DATA_KEY);
                itemStack.setItemMeta(meta);
            }
        } else {
            ItemMeta meta = itemStack.hasItemMeta() ?
                            Objects.requireNonNull(itemStack.getItemMeta()) :
                            Bukkit.getItemFactory().getItemMeta(itemStack.getType());
            Objects.requireNonNull(meta).getPersistentDataContainer().set(ITEM_DATA_KEY,
                                                                          PersistentDataType.STRING,
                                                                          gson.toJson(data, ItemData.class));
            itemStack.setItemMeta(meta);
        }
    }

    @Override
    public void setOreDictionary(@Nonnull ItemStack itemStack, @Nonnull Set<String> oreDictionary) {
        ItemMeta meta = itemStack.hasItemMeta() ?
                        Objects.requireNonNull(itemStack.getItemMeta()) :
                        Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        Objects.requireNonNull(meta).getPersistentDataContainer().set(ITEM_ORE_DICTIONARY_KEY,
                                                                      PersistentDataType.STRING,
                                                                      gson.toJson(oreDictionary,
                                                                                  new TypeToken<Set<String>>() {
                                                                                  }.getType()));
        itemStack.setItemMeta(meta);
    }
}
