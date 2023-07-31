package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.i18n.I18n;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EnchantmentUtil {
    private static final LinkedHashMap<Integer, String> lookup = new LinkedHashMap<>();

    static {
        lookup.put(1000, "M");
        lookup.put(900, "CM");
        lookup.put(500, "D");
        lookup.put(400, "CD");
        lookup.put(100, "C");
        lookup.put(90, "XC");
        lookup.put(50, "L");
        lookup.put(40, "XL");
        lookup.put(10, "X");
        lookup.put(9, "IX");
        lookup.put(5, "V");
        lookup.put(4, "IV");
        lookup.put(1, "I");
    }

    public static CustomEnchantment create(@Nonnull NamespacedKey id, @Nonnull NamespacedKey localizedNameKey) {
        return new CustomEnchantment(id, localizedNameKey);
    }

    public static String getLoreOfEnchant(CustomEnchantment enchantment, int level) {
        return ChatColor.RESET.toString() +
               (enchantment.isTreasure() ? ChatColor.GOLD : (enchantment.isCursed() ? ChatColor.RED : ChatColor.GRAY)) +
               I18n.getLocalePlaceholder(enchantment.getLocalizedNameKey()) + " " + getLevelStr(level);
    }

    private static String getLevelStr(int level) {
        if (level >= 32767) return level + "L";

        StringBuilder res = new StringBuilder();
        int temp = level;
        for (Map.Entry<Integer, String> entry : lookup.entrySet()) {
            Integer k = entry.getKey();
            res.append(StringUtils.repeat(entry.getValue(), temp / k));
            temp %= k;
            if (temp == 0) break;
        }
        return res.toString();
    }

    /**
     * Add the given custom enchantment of the given level to the given item
     */
    public static void addToItem(ItemStack item, CustomEnchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = Objects.requireNonNull(meta).hasLore() ? meta.getLore() : new ArrayList<>();
            Objects.requireNonNull(lore).add(getLoreOfEnchant(enchantment, level));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    /**
     * Remove the given custom enchantment of the given level on the given item <br/>
     * <br/>
     * The item must has the given enchantment on it
     */
    public static void removeFromItem(ItemStack item, CustomEnchantment enchantment, int level) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                assert lore != null;
                lore.remove(getLoreOfEnchant(enchantment, level));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
        item.removeEnchantment(enchantment);
    }

    /**
     * Remove the given custom enchantment on the given item <br/>
     * <br/>
     * The item must has the given enchantment on it
     */
    public static void removeFromItem(ItemStack item, CustomEnchantment enchantment) {
        removeFromItem(item, enchantment, item.getEnchantmentLevel(enchantment));
    }

    public static class CustomEnchantment extends Enchantment {
        private final NamespacedKey id;
        private final NamespacedKey localizedNameKey;
        private int startLevel = 1;
        private int maxLevel = 1;
        @SuppressWarnings("deprecation")
        private EnchantmentTarget target = EnchantmentTarget.ALL;
        private boolean treasure = false;
        private boolean cursed = false;
        private List<Enchantment> conflictEnchantments = Collections.emptyList();
        private List<ItemStack> otherItems = Collections.emptyList();

        public CustomEnchantment(@Nonnull NamespacedKey id, @Nonnull NamespacedKey localizedNameKey) {
            super(id);
            this.id = id;
            this.localizedNameKey = localizedNameKey;
        }

        @Nonnull
        @Override
        public String getName() {
            return id.getKey();
        }

        @Nonnull
        public NamespacedKey getLocalizedNameKey() {
            return localizedNameKey;
        }

        @Override
        public int getMaxLevel() {
            return maxLevel;
        }

        @Override
        public int getStartLevel() {
            return startLevel;
        }

        @Nonnull
        @Override
        public EnchantmentTarget getItemTarget() {
            return target;
        }

        @Override
        public boolean isTreasure() {
            return treasure;
        }

        @Override
        public boolean isCursed() {
            return cursed;
        }

        @Override
        public boolean conflictsWith(@Nonnull Enchantment enchantment) {
            return conflictEnchantments.contains(enchantment);
        }

        @Override
        public boolean canEnchantItem(@Nonnull ItemStack itemStack) {
            return target.includes(itemStack) || otherItems.stream().anyMatch(itemStack::isSimilar);
        }

        public CustomEnchantment startLevel(int startLevel) {
            this.startLevel = startLevel;
            return this;
        }

        public CustomEnchantment maxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public CustomEnchantment itemTarget(EnchantmentTarget target) {
            this.target = target;
            return this;
        }

        public CustomEnchantment treasure() {
            this.treasure = true;
            return this;
        }

        public CustomEnchantment cursed() {
            this.cursed = true;
            return this;
        }

        public CustomEnchantment conflictEnchantments(List<Enchantment> conflictEnchantment) {
            this.conflictEnchantments = new ArrayList<>(conflictEnchantment);
            return this;
        }

        public CustomEnchantment otherItems(List<ItemStack> otherItems) {
            this.otherItems = otherItems.stream().map(ItemStack::clone).collect(Collectors.toList());
            return this;
        }

        public CustomEnchantment register() {
            if (!Enchantment.isAcceptingRegistrations()) try {
                Field field = Enchantment.class.getDeclaredField("acceptingNew");
                field.setAccessible(true);
                field.set(Enchantment.class, true);
            } catch (Exception e) {
                WheelCore.getInstance().getLogger().log(Level.SEVERE, e, () -> "Error registering custom enchantment");
            }

            Enchantment.registerEnchantment(this);
            return this;
        }
    }
}