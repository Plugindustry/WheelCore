package com.IndustrialWorld.manager.recipe;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface CraftingRecipe extends RecipeBase {
	ShapedRecipe.MatchInfo matches(List<List<ItemStack>> recipe, Map<Integer, ItemStack> damageResult);

	CraftingRecipe addItemCost(ItemStack is, int durability);
}
