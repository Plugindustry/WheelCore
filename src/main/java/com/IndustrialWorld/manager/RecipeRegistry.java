package com.IndustrialWorld.manager;

import com.IndustrialWorld.manager.recipe.CraftingRecipe;
import com.IndustrialWorld.manager.recipe.RecipeBase;
import com.IndustrialWorld.manager.recipe.SmeltingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecipeRegistry {
	private static List<RecipeBase> recipes = new LinkedList<>();

	public static void register(RecipeBase recipeBase) {
		recipes.add(recipeBase);
	}

	public static CraftingRecipe matchCraftingRecipe(List<ItemStack> items, Map<Integer, ItemStack> damageResult) {
		// tidy up the matrix
		List<List<ItemStack>> matrix = new LinkedList<>();
		List<ItemStack> current = new LinkedList<>();
		int i = 0;
		do {
			if (i % 3 == 0 && i != 0) {
				matrix.add(current);
				current = new LinkedList<>();
			}


			current.add(items.get(i));
			i++;
		} while (i < 9);

		matrix.add(current);

		// then match the recipe
		for (RecipeBase recipeBase : recipes) {
			if (!(recipeBase instanceof CraftingRecipe)) {
				continue;
			}

			if (((CraftingRecipe) recipeBase).matches(matrix, damageResult)) {
				return (CraftingRecipe) recipeBase;
			}
		}

		return null;
	}

	public static SmeltingRecipe matchSmeltingRecipe(ItemStack origin, ItemStack fuel) {
		for (RecipeBase recipeBase : recipes) {
			if (recipeBase instanceof SmeltingRecipe) {
				if (((SmeltingRecipe) recipeBase).matches(origin, fuel)) {
					return (SmeltingRecipe) recipeBase;
				}
			}
		}

		return null;
	}
}
