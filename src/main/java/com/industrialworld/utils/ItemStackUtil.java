package com.industrialworld.utils;

import com.industrialworld.item.ItemType;
import com.industrialworld.item.material.IWMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ItemStackUtil {
    public static ItemStackFactory create(Material mtrl) {
        return new ItemStackFactory(mtrl);
    }

    public static boolean isIWItem(ItemStack item) {
        NBTUtil.NBTValue value = NBTUtil.getTagValue(item, "isIWItem");
        return value != null && value.asBoolean();
    }

    public static boolean isSimilar(ItemStack a, ItemStack b) {
        if (a == null) {
            return b == null;
        }

        if (!a.isSimilar(b)) {
            return false;
        }

        // check NBT tag
        boolean aIs = isIWItem(a);
        if (aIs != isIWItem(b))
            return false;

        if (aIs) {
            NBTUtil.NBTValue aId = NBTUtil.getTagValue(a, "IWItemId");
            NBTUtil.NBTValue bId = NBTUtil.getTagValue(b, "IWItemId");
            return (aId == null && bId == null) ||
                   (aId != null && bId != null && Objects.equals(aId.asString(), bId.asString()));
        }
        return true;
    }

    public static ItemType getItemType(ItemStack is) {
        NBTUtil.NBTValue nbtValue = NBTUtil.getTagValue(is, "IWItemType");
        if (nbtValue == null) {
            return ItemType.NULL;
        } else {
            return ItemType.valueOf(nbtValue.asString());
        }
    }

    public static IWMaterial getItemMaterial(ItemStack is) {
        NBTUtil.NBTValue nbtValue = NBTUtil.getTagValue(is, "IWMaterial");
        if (nbtValue == null) {
            return IWMaterial.NULL;
        } else {
            return IWMaterial.valueOf(nbtValue.asString());
        }
    }

    public static class ItemStackFactory {
        private Material mtrl;
        private int amount = 1;
        private String id;
        private String displayName = "";
        private List<String> lore = Collections.singletonList("");
        private IWMaterial iwMaterial = IWMaterial.NULL;
        private ItemType itemType = ItemType.NULL;

        public ItemStackFactory(Material mtrl) {
            this.mtrl = mtrl;
        }

        public ItemStackFactory setAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemStackFactory setId(String id) {
            this.id = id;
            return this;
        }

        public ItemStackFactory setLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemStackFactory setDisplayName(String dpName) {
            this.displayName = dpName;
            return this;
        }

        public ItemStackFactory setItemType(ItemType itemType) {
            this.itemType = itemType;
            return this;
        }

        public ItemStackFactory setIWMaterial(IWMaterial iwMaterial) {
            this.iwMaterial = iwMaterial;
            return this;
        }

        public ItemStack getItemStack() {
            ItemStack tmp = new ItemStack(mtrl, amount);
            tmp = NBTUtil.setTagValue(tmp, "isIWItem", new NBTUtil.NBTValue().set(true));
            tmp = NBTUtil.setTagValue(tmp, "IWItemId", new NBTUtil.NBTValue().set(id));
            tmp = NBTUtil.setTagValue(tmp, "IWMaterial", new NBTUtil.NBTValue().set(iwMaterial.getMaterialID()));
            tmp = NBTUtil.setTagValue(tmp, "IWItemType", new NBTUtil.NBTValue().set(itemType.getTypeID()));
            ItemMeta meta = tmp.getItemMeta();
            if (meta == null) {
                meta = Bukkit.getItemFactory().getItemMeta(mtrl);
                if (meta == null) {
                    throw new IllegalStateException("¿");
                }
            }
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            tmp.setItemMeta(meta);
            return tmp.clone();
        }
    }
}
