package io.github.plugindustry.wheelcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemStackUtil {
    public static ItemStackFactory create(Material mtrl) {
        return new ItemStackFactory(mtrl);
    }

    public static boolean isPIItem(ItemStack item) {
        return NBTUtil.getTagValue(item, "PIItemId") != null;
    }

    public static String getPIItemId(ItemStack item) {
        return isPIItem(item) ? Objects.requireNonNull(NBTUtil.getTagValue(item, "PIItemId")).asString() : "";
    }

    public static List<String> getOreDictionary(ItemStack item) {
        NBTUtil.NBTValue value = NBTUtil.getTagValue(item, "OreDictionary");
        return value == null ?
               Collections.emptyList() :
               value.asList().stream().map(NBTUtil.NBTValue::asString).collect(Collectors.toList());
    }

    public static boolean isSimilar(ItemStack a, ItemStack b) {
        return isSimilar(a, b, false);
    }

    public static boolean isSimilar(ItemStack a, ItemStack b, boolean ignoreDurability) {
        if (a == null) {
            return b == null;
        }

        ItemStack ta = a.clone();
        ItemStack tb = b.clone();
        if (ignoreDurability) {
            ta.setDurability((short) 0);
            tb.setDurability((short) 0);
        }

        if (!ta.isSimilar(tb)) {
            return false;
        }

        // check NBT tag
        boolean aIs = isPIItem(ta);
        if (aIs != isPIItem(tb))
            return false;

        if (aIs) {
            NBTUtil.NBTValue aId = NBTUtil.getTagValue(ta, "PIItemId");
            NBTUtil.NBTValue bId = NBTUtil.getTagValue(tb, "PIItemId");
            return (aId == null && bId == null) || (aId != null && bId != null && Objects.equals(aId.asString(),
                                                                                                 bId.asString()));
        }
        return true;
    }

    public static class ItemStackFactory {
        private final Material material;
        private int amount = 1;
        private String id = null;
        private String displayName = "";
        private List<String> lore = Collections.singletonList("");
        private List<NBTUtil.NBTValue> oreDictionary = null;

        public ItemStackFactory(Material material) {
            this.material = material;
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

        public ItemStackFactory setOreDictionary(String... dictionary) {
            return setOreDictionary(Arrays.asList(dictionary));
        }

        public ItemStackFactory setOreDictionary(List<String> dictionary) {
            oreDictionary = dictionary.stream().map(NBTUtil.NBTValue::of).sorted().collect(Collectors.toList());
            return this;
        }

        public ItemStack getItemStack() {
            ItemStack tmp = new ItemStack(material, amount);
            if (id != null) {
                tmp = NBTUtil.setTagValue(tmp, "PIItemId", NBTUtil.NBTValue.of(id));
                tmp = NBTUtil.setTagValue(tmp, "OreDictionary", NBTUtil.NBTValue.of(oreDictionary));
            }
            ItemMeta meta = tmp.getItemMeta();
            if (meta == null) {
                meta = Bukkit.getItemFactory().getItemMeta(material);
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
