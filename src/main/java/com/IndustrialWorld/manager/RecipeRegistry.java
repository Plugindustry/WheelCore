package com.IndustrialWorld.manager;

import com.IndustrialWorld.manager.recipe.CraftingRecipe;
import com.IndustrialWorld.manager.recipe.RecipeBase;
import com.IndustrialWorld.manager.recipe.SmeltingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class RecipeRegistry {
	private static List<RecipeBase> recipes = new LinkedList<>();

	public static void register(RecipeBase recipeBase) {
		recipes.add(recipeBase);
	}

	public static RecipeBase matchCraftingRecipe(List<ItemStack> items, List<ItemStack> damageResult) {
		// tidy up the matrix
		List<List<ItemStack>> matrix = new LinkedList<>();
		List<ItemStack> current = null;
		int i = 0;
		do {
			if (i % 3 == 0) {
				current = new LinkedList<>();
				matrix.add(current);
			}

			current.add(items.get(i));
			i++;
		} while (i < 9);

		// then match the recipe
		for (RecipeBase recipeBase : recipes) {
			if (!(recipeBase instanceof CraftingRecipe)) {
				continue;
			}

			if (((CraftingRecipe) recipeBase).matches(matrix, damageResult)) {
				return recipeBase;
			}
		}

		return null;
	}

	public static RecipeBase matchSmeltingRecipe(ItemStack origin, ItemStack fuel) {
		for (RecipeBase recipeBase : recipes) {
			if (recipeBase instanceof SmeltingRecipe) {
				if (((SmeltingRecipe) recipeBase).matches(origin, fuel)) {
					return recipeBase;
				}
			}
		}

		return null;
	}
}
