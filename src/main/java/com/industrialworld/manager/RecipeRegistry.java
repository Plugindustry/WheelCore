package com.industrialworld.manager;

import com.industrialworld.IndustrialWorld;
import com.industrialworld.item.material.IWMaterial;
import com.industrialworld.manager.recipe.CraftingRecipe;
import com.industrialworld.manager.recipe.GrindRecipe;
import com.industrialworld.manager.recipe.RecipeBase;
import com.industrialworld.manager.recipe.SmeltingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecipeRegistry {
    private static List<RecipeBase> recipes = new LinkedList<>();
    public static NamespacedKey namespace = new NamespacedKey(IndustrialWorld.instance, "IndustrialWorld");

    public static void register(RecipeBase recipeBase) {
        recipes.add(recipeBase);
        if (recipeBase instanceof SmeltingRecipe)
            Bukkit.addRecipe(new FurnaceRecipe(namespace, recipeBase.getResult(IWMaterial.NULL), new RecipeChoice.ExactChoice(((SmeltingRecipe) recipeBase).getAllMatches()), ((SmeltingRecipe) recipeBase).getExperience(), ((SmeltingRecipe) recipeBase).getCookingTime()));
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
            if (recipeBase instanceof SmeltingRecipe && ((SmeltingRecipe) recipeBase).matches(origin)) {
                return (SmeltingRecipe) recipeBase;
            }
        }

        return null;
    }

    public static GrindRecipe matchGrindRecipe(ItemStack origin) {
        for (RecipeBase recipeBase : recipes) {
            if (recipeBase instanceof GrindRecipe && ((GrindRecipe) recipeBase).matches(origin)) {
                return (GrindRecipe) recipeBase;
            }
        }

        return null;
    }
}
