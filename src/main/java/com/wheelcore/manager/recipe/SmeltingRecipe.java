package com.wheelcore.manager.recipe;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface SmeltingRecipe extends RecipeBase {
    boolean matches(ItemStack origin);

    float getExperience();

    int getCookingTime();

    List<ItemStack> getAllMatches();
}
