package com.industrialworld.manager.recipe;

import com.industrialworld.item.material.IWMaterial;
import org.bukkit.inventory.ItemStack;

public class SmeltingRecipeImpl implements SmeltingRecipe {
    private ItemStack recipe;
    private ItemStack result;

    public SmeltingRecipeImpl(ItemStack recipe, ItemStack result) {
        this.recipe = recipe;
        this.result = result;
    }

    @Override
    public boolean matches(ItemStack origin) {
        return origin.isSimilar(recipe);
    }

    @Override
    public ItemStack getResult(IWMaterial iwMaterial) {
        return result;
    }
}
