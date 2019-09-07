package com.IndustrialWorld.manager.recipe;

import org.bukkit.inventory.ItemStack;

public interface SmeltingRecipe extends RecipeBase {
	boolean matches(ItemStack origin, ItemStack fuel);
}
