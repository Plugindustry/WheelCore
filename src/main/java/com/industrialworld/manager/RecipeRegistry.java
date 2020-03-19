package com.industrialworld.manager;

import com.industrialworld.manager.recipe.CraftingRecipe;
import com.industrialworld.manager.recipe.GrindRecipe;
import com.industrialworld.manager.recipe.RecipeBase;
import com.industrialworld.manager.recipe.SmeltingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecipeRegistry {
    private static List<RecipeBase> recipes = new LinkedList<>();

    public static void register(RecipeBase recipeBase) {
        recipes.add(recipeBase);
    }

    public static RecipeBase.RecipeResultInfo matchCraftingRecipe(List<ItemStack> items, Map<Integer, ItemStack> damageResult) {
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

            RecipeBase.MatchInfo info = ((CraftingRecipe) recipeBase).matches(matrix, damageResult);
            if (info.isMatches()) {
                if (info.isHasIWMaterial()) {
                    return new RecipeBase.RecipeResultInfo((CraftingRecipe) recipeBase, info.getIwMaterial());
                }
                return new RecipeBase.RecipeResultInfo((CraftingRecipe) recipeBase, null);
            }
        }

        return null;
    }

    public static SmeltingRecipe matchSmeltingRecipe(ItemStack origin) {
        for (RecipeBase recipeBase : recipes) {
            if (recipeBase instanceof SmeltingRecipe) {
                if (((SmeltingRecipe) recipeBase).matches(origin)) {
                    return (SmeltingRecipe) recipeBase;
                }
            }
        }

        return null;
    }

    public static GrindRecipe matchGrindRecipe(ItemStack origin) {
        for (RecipeBase recipeBase : recipes) {
            if (recipeBase instanceof GrindRecipe) {
                if (((GrindRecipe) recipeBase).matches(origin)) {
                    return (GrindRecipe) recipeBase;
                }
            }
        }

        return null;
    }
}
