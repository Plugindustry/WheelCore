package com.wheelcore.manager.recipe.choice;

import com.wheelcore.utils.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

public class ItemStackChoice implements RecipeChoice {
    private final ItemStack item;
    private boolean exact = false;

    public ItemStackChoice(ItemStack item) {
        this.item = item.clone();
    }

    public ItemStackChoice(ItemStack item, boolean exact) {
        this(item);
        this.exact = exact;
    }

    @Override
    public boolean matches(ItemStack item) {
        return ItemStackUtil.isSimilar(this.item, item, !exact);
    }

    @Override
    public org.bukkit.inventory.RecipeChoice.MaterialChoice getPlaceholderChoice() {
        return new org.bukkit.inventory.RecipeChoice.MaterialChoice(item.getType());
    }
}
