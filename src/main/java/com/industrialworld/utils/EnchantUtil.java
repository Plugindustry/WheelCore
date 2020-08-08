package com.industrialworld.utils;

import com.industrialworld.IndustrialWorld;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class EnchantUtil {
    public static CustomEnchantment create(String name) {
        return new CustomEnchantment(name);
    }

    public static class CustomEnchantment extends Enchantment {
        private final String name;
        private int startLevel = 1;
        private int maxLevel = 1;
        private EnchantmentTarget target = EnchantmentTarget.ALL;
        private boolean treasure = false;
        private boolean cursed = false;
        private List<Enchantment> conflictEnchantments = Collections.emptyList();
        private List<ItemStack> otherItems = Collections.emptyList();

        public CustomEnchantment(String name) {
            super(new NamespacedKey(IndustrialWorld.instance, name));
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
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
            this.conflictEnchantments = conflictEnchantment;
            return this;
        }

        public CustomEnchantment otherItems(List<ItemStack> otherItems) {
            this.otherItems = otherItems;
            return this;
        }

        public CustomEnchantment register() {
            if (!Enchantment.isAcceptingRegistrations())
                try {
                    Field field = Enchantment.class.getDeclaredField("acceptingNew");
                    field.setAccessible(true);
                    field.set(Enchantment.class, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            Enchantment.registerEnchantment(this);
            return this;
        }
    }
}
