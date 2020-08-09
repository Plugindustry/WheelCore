package com.industrialworld.manager.recipe;

import com.industrialworld.item.ItemType;
import com.industrialworld.item.material.IWMaterial;
import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GrindRecipeImpl implements GrindRecipe {
    private double power;
    private Object recipe;
    private Object result;

    public GrindRecipeImpl(double power, Object recipe, Object result) {
        this.power = power;
        this.recipe = recipe;
        this.result = result;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        if (recipe instanceof ItemStack)
            return ItemStackUtil.isSimilar((ItemStack) recipe, itemStack);
        else if (recipe instanceof ItemType)
            return ItemStackUtil.getItemType(itemStack) == recipe;
        else
            return false;
    }

    @Override
    public double getPowerNeeded() {
        return power;
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
