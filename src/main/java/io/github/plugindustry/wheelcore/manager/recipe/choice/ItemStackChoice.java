package io.github.plugindustry.wheelcore.manager.recipe.choice;

import io.github.plugindustry.wheelcore.utils.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

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
    public boolean matches(@Nonnull ItemStack item) {
        return ItemStackUtil.isSimilar(this.item, item, !exact);
    }

    @Nonnull
    @Override
    public org.bukkit.inventory.RecipeChoice.MaterialChoice getPlaceholderChoice() {
        return new org.bukkit.inventory.RecipeChoice.MaterialChoice(item.getType());
    }
}
