package com.industrialworld.manager.recipe;

import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

public class GrindRecipeImpl implements GrindRecipe {
    private final double power;
    private final ItemStack recipe;
    private final ItemStack result;

    public GrindRecipeImpl(double power, ItemStack recipe, ItemStack result) {
        this.power = power;
        this.recipe = recipe;
        this.result = result;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        return ItemStackUtil.isSimilar(recipe, itemStack);
    }

    @Override
    public double getPowerNeeded() {
        return power;
    }

    @Override
    public ItemStack getResult() {
        return result.clone();
    }
}
