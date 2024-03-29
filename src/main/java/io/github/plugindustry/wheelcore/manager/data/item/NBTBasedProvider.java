package io.github.plugindustry.wheelcore.manager.data.item;

import com.comphenix.protocol.wrappers.nbt.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.czm23333.transparentreflect.ShadowManager;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.interfaces.item.ItemData;
import io.github.plugindustry.wheelcore.internal.shadow.CraftItemStack;
import io.github.plugindustry.wheelcore.manager.ItemMapping;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.GsonHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NBTBasedProvider implements ItemDataProvider {
    private static final Gson gson;

    static {
        GsonBuilder gbs = GsonHelper.bukkitCompat();
        gbs.registerTypeAdapter(ItemData.class, GsonHelper.POLYMORPHISM_SERIALIZER);
        gbs.registerTypeAdapter(ItemData.class, GsonHelper.POLYMORPHISM_DESERIALIZER);
        gson = gbs.create();
    }

    @Nullable
    @Override
    public ItemBase getInstance(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;
        Optional<NbtWrapper<?>> nbtWrapperOptional = NbtFactory.fromItemOptional(
                (ItemStack) ShadowManager.shadowUnpack(CraftItemStack.asCraftCopy(itemStack)));
        if (nbtWrapperOptional.isEmpty()) return null;
        NbtWrapper<?> nbtWrapper = nbtWrapperOptional.get();
        if (nbtWrapper.getType() != NbtType.TAG_COMPOUND) return null;
        NbtCompound compound = NbtFactory.asCompound(nbtWrapper);
        if (!compound.containsKey("wheel_core_item_type")) return null;
        Object type = compound.getObject("wheel_core_item_type");
        return type instanceof String ? MainManager.getItemInstanceFromId(NamespacedKey.fromString((String) type)) :
                null;
    }

    @Nullable
    @Override
    public ItemData getData(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;
        Optional<NbtWrapper<?>> nbtWrapperOptional = NbtFactory.fromItemOptional(
                (ItemStack) ShadowManager.shadowUnpack(CraftItemStack.asCraftCopy(itemStack)));
        if (nbtWrapperOptional.isEmpty()) return null;
        NbtWrapper<?> nbtWrapper = nbtWrapperOptional.get();
        if (nbtWrapper.getType() != NbtType.TAG_COMPOUND) return null;
        NbtCompound compound = NbtFactory.asCompound(nbtWrapper);
        if (!compound.containsKey("wheel_core_item_data")) return null;
        Object data = compound.getObject("wheel_core_item_data");
        return data instanceof String ? gson.fromJson((String) data, ItemData.class) : null;
    }

    @Nullable
    @Override
    public ItemData getAdditionalData(@Nullable ItemStack itemStack, @Nonnull NamespacedKey key) {
        if (itemStack == null) return null;
        Optional<NbtWrapper<?>> nbtWrapperOptional = NbtFactory.fromItemOptional(
                (ItemStack) ShadowManager.shadowUnpack(CraftItemStack.asCraftCopy(itemStack)));
        if (nbtWrapperOptional.isEmpty()) return null;
        NbtWrapper<?> nbtWrapper = nbtWrapperOptional.get();
        if (nbtWrapper.getType() != NbtType.TAG_COMPOUND) return null;
        NbtCompound compound = NbtFactory.asCompound(nbtWrapper);
        if (!compound.containsKey("wheel_core_item_additional_data")) return null;
        NbtCompound additional = compound.getCompound("wheel_core_item_additional_data");
        String keyStr = key.toString();
        if (!additional.containsKey(keyStr)) return null;
        Object data = additional.getObject(keyStr);
        return data instanceof String ? gson.fromJson((String) data, ItemData.class) : null;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getOreDictionary(@Nullable ItemStack itemStack) {
        if (itemStack == null) return Collections.emptySet();
        Set<String> defaultOreDict =
                getInstance(itemStack) == null ? ItemMapping.getVanillaOreDict(itemStack.getType()) :
                        Collections.emptySet();
        Optional<NbtWrapper<?>> nbtWrapperOptional = NbtFactory.fromItemOptional(
                (ItemStack) ShadowManager.shadowUnpack(CraftItemStack.asCraftCopy(itemStack)));
        if (nbtWrapperOptional.isEmpty()) return defaultOreDict;
        NbtWrapper<?> nbtWrapper = nbtWrapperOptional.get();
        if (nbtWrapper.getType() != NbtType.TAG_COMPOUND) return defaultOreDict;
        NbtCompound compound = NbtFactory.asCompound(nbtWrapper);
        if (!compound.containsKey("wheel_core_item_ore_dictionary")) return defaultOreDict;
        Object data = compound.getObject("wheel_core_item_ore_dictionary");
        if ((!(data instanceof NbtList)) || ((NbtList<?>) data).getElementType() != NbtType.TAG_STRING)
            return defaultOreDict;
        return ((NbtList<String>) data).asCollection().stream().map(NbtBase::getValue).collect(Collectors.toSet());
    }

    @Override
    public void setInstance(@Nonnull ItemStack itemStack, @Nullable ItemBase instance) {
        ItemStack newItemStack = (ItemStack) ShadowManager.shadowUnpack(CraftItemStack.asCraftCopy(itemStack));
        NbtWrapper<?> nbtWrapper = NbtFactory.fromItemTag(newItemStack);
        NbtCompound compound = nbtWrapper.getType() == NbtType.TAG_COMPOUND ? NbtFactory.asCompound(nbtWrapper) :
                NbtFactory.ofCompound("tag");
        if (instance == null) compound.remove("wheel_core_item_type");
        else {
            NamespacedKey key = Objects.requireNonNull(MainManager.getIdFromInstance(instance));
            compound.put("wheel_core_item_type", key.toString());
        }
        NbtFactory.setItemTag(newItemStack, compound);
        itemStack.setItemMeta(newItemStack.getItemMeta());
    }

    @Override
    public void setData(@Nonnull ItemStack itemStack, @Nullable ItemData data) {
        ItemStack newItemStack = (ItemStack) ShadowManager.shadowUnpack(CraftItemStack.asCraftCopy(itemStack));
        NbtWrapper<?> nbtWrapper = NbtFactory.fromItemTag(newItemStack);
        NbtCompound compound = nbtWrapper.getType() == NbtType.TAG_COMPOUND ? NbtFactory.asCompound(nbtWrapper) :
                NbtFactory.ofCompound("tag");
        if (data == null) compound.remove("wheel_core_item_data");
        else compound.put("wheel_core_item_data", gson.toJson(data, ItemData.class));
        NbtFactory.setItemTag(newItemStack, compound);
        itemStack.setItemMeta(newItemStack.getItemMeta());
    }

    @Override
    public void setAdditionalData(@Nonnull ItemStack itemStack, @Nonnull NamespacedKey key, @Nullable ItemData data) {
        ItemStack newItemStack = (ItemStack) ShadowManager.shadowUnpack(CraftItemStack.asCraftCopy(itemStack));
        NbtWrapper<?> nbtWrapper = NbtFactory.fromItemTag(newItemStack);
        NbtCompound compound = nbtWrapper.getType() == NbtType.TAG_COMPOUND ? NbtFactory.asCompound(nbtWrapper) :
                NbtFactory.ofCompound("tag");
        NbtCompound additional;
        if (compound.containsKey("wheel_core_item_additional_data"))
            additional = compound.getCompound("wheel_core_item_additional_data");
        else additional = NbtFactory.ofCompound("tag");
        String keyStr = key.toString();
        if (data == null) additional.remove(keyStr);
        else additional.put(keyStr, gson.toJson(data, ItemData.class));
        if (additional.getKeys().isEmpty()) compound.remove("wheel_core_item_additional_data");
        else compound.put("wheel_core_item_additional_data", additional);
        NbtFactory.setItemTag(newItemStack, compound);
        itemStack.setItemMeta(newItemStack.getItemMeta());
    }

    @Override
    public void setOreDictionary(@Nonnull ItemStack itemStack, @Nonnull Set<String> oreDictionary) {
        ItemStack newItemStack = (ItemStack) ShadowManager.shadowUnpack(CraftItemStack.asCraftCopy(itemStack));
        NbtWrapper<?> nbtWrapper = NbtFactory.fromItemTag(newItemStack);
        NbtCompound compound = nbtWrapper.getType() == NbtType.TAG_COMPOUND ? NbtFactory.asCompound(nbtWrapper) :
                NbtFactory.ofCompound("tag");
        compound.put("wheel_core_item_ore_dictionary",
                NbtFactory.ofList("wheel_core_item_ore_dictionary", oreDictionary));
        NbtFactory.setItemTag(newItemStack, compound);
        itemStack.setItemMeta(newItemStack.getItemMeta());
    }
}