package io.github.plugindustry.wheelcore.utils;

import com.google.common.collect.Sets;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ItemStackUtil {
    public static ItemStackFactory create(Material material) {
        return new ItemStackFactory(material);
    }

    public static boolean isSimilar(ItemStack a, ItemStack b) {
        return isSimilar(a, b, false);
    }

    public static boolean isSimilar(ItemStack a, ItemStack b, boolean ignoreDurability) {
        if (a == null) {
            return b == null;
        } else if (b == null) {
            return false;
        }

        ItemStack ta = a.clone();
        ItemStack tb = b.clone();
        if (ignoreDurability) {
            ItemStackUtil.setDurability(ta, 0);
            ItemStackUtil.setDurability(tb, 0);
        }

        if (!ta.isSimilar(tb)) {
            return false;
        }

        return MainManager.getItemInstance(ta) == MainManager.getItemInstance(tb);
    }

    public static int getDurability(ItemStack itemStack) {
        return itemStack.getItemMeta() instanceof Damageable ? ((Damageable) itemStack.getItemMeta()).getDamage() : 0;
    }

    public static void setDurability(ItemStack itemStack, int damage) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
            if (meta == null) {
                throw new IllegalStateException("¿");
            }
        }

        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(damage);
            itemStack.setItemMeta(meta);
        }
    }

    public static class ItemStackFactory {
        private final Material material;
        private int amount = 1;
        private ItemBase instance = null;
        private String displayName = null;
        private List<String> lore = Collections.emptyList();
        private Set<String> oreDictionary = null;
        private Integer customModelData = null;

        public ItemStackFactory(Material material) {
            this.material = material;
        }

        public ItemStackFactory amount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemStackFactory instance(ItemBase instance) {
            this.instance = instance;
            return this;
        }

        public ItemStackFactory lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemStackFactory displayName(String dpName) {
            this.displayName = dpName;
            return this;
        }

        public ItemStackFactory oreDictionary(String... dictionary) {
            return oreDictionary(Sets.newHashSet(dictionary));
        }

        public ItemStackFactory oreDictionary(Set<String> dictionary) {
            oreDictionary = dictionary;
            return this;
        }

        public ItemStackFactory customModelData(String id) {
            customModelData = CRC24.calculateForString(id);
            return this;
        }

        public ItemStackFactory customModelData(int id) {
            customModelData = id;
            return this;
        }

        public ItemStack getItemStack() {
            ItemStack tmp = new ItemStack(material, amount);
            if (instance != null)
                MainManager.setItemInstance(tmp, instance);
            if (oreDictionary != null)
                MainManager.setItemOreDictionary(tmp, oreDictionary);
            ItemMeta meta = tmp.getItemMeta();
            if (meta == null) {
                meta = Bukkit.getItemFactory().getItemMeta(material);
                if (meta == null)
                    throw new IllegalStateException("¿");
            }
            if (displayName != null)
                meta.setDisplayName(displayName);
            if (customModelData != null)
                meta.setCustomModelData(customModelData);
            meta.setLore(lore);
            tmp.setItemMeta(meta);
            return tmp.clone();
        }
    }
}
