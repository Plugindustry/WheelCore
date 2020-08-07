package com.industrialworld.manager.recipe;

import com.industrialworld.item.ItemType;
import com.industrialworld.item.material.IWMaterial;
import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class SmeltingRecipeImpl implements SmeltingRecipe {
    private Object recipe;
    private Object result;
    private float experience;
    private int cookingTime;

    public SmeltingRecipeImpl(ItemStack recipe, ItemStack result, float experience, int cookingTime) {
        this.recipe = recipe;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(ItemStack origin) {
        if (recipe instanceof ItemStack)
            return origin.isSimilar((ItemStack) recipe);
        else if (recipe instanceof ItemType)
            return ItemStackUtil.getItemType(origin) == recipe;
        else
            return false;
    }

    @Override
    public ItemStack getResult(IWMaterial iwMaterial) {
        if (result instanceof ItemStack)
            return ((ItemStack) result).clone();
        else if (recipe instanceof ItemType)
            return ((ItemType) recipe).getTemplate().getItemStack(iwMaterial);
        else
            return new ItemStack(Material.AIR);
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
        if (recipe instanceof ItemStack)
            return Collections.singletonList((ItemStack) recipe);
        else if (recipe instanceof ItemType)
            return ((ItemType) recipe).getTemplate().getAllItems();
        else
            return Collections.emptyList();
    }
}
