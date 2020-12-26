package com.industrialworld.manager.recipe;

import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class SmeltingRecipeImpl implements SmeltingRecipe {
    private final ItemStack recipe;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;

    public SmeltingRecipeImpl(ItemStack recipe, ItemStack result, float experience, int cookingTime) {
        this.recipe = recipe;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(ItemStack origin) {
        return ItemStackUtil.isSimilar(recipe, origin);
    }

    @Override
    public ItemStack getResult() {
        return result.clone();
    }

    @Override
    public float getExperience() {
        return experience;
    }

    @Override
    public int getCookingTime() {
        return cookingTime;
    }

    @Override
    public List<ItemStack> getAllMatches() {
        return Collections.singletonList(recipe);
    }
}
