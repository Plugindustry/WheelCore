package com.industrialworld.manager.recipe;

import com.industrialworld.item.ItemType;
import com.industrialworld.item.material.IWMaterial;
import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SmeltingRecipeImpl implements SmeltingRecipe {
    private Object recipe;
    private Object result;

    public SmeltingRecipeImpl(ItemStack recipe, ItemStack result) {
        this.recipe = recipe;
        this.result = result;
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
}
