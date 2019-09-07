package com.IndustrialWorld.manager.recipe;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface CraftingRecipe extends RecipeBase {
	boolean matches(List<List<ItemStack>> recipe, List<ItemStack> damageResult);

	void addItemCost(ItemStack is, int durability);
}
