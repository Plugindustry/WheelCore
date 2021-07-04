package io.github.plugindustry.wheelcore.manager;

import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.manager.recipe.CraftingRecipe;
import io.github.plugindustry.wheelcore.manager.recipe.MatrixInputRecipe;
import io.github.plugindustry.wheelcore.manager.recipe.RecipeBase;
import io.github.plugindustry.wheelcore.manager.recipe.ShapedRecipe;
import io.github.plugindustry.wheelcore.manager.recipe.ShapelessRecipe;
import io.github.plugindustry.wheelcore.manager.recipe.SingleInputRecipe;
import io.github.plugindustry.wheelcore.manager.recipe.SmeltingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecipeRegistry {
    private static final List<RecipeBase> recipes = new LinkedList<>();
    private static final Set<NamespacedKey> placeholders = new HashSet<>();

    /**
     * @param recipeBase      The recipe to register
     * @param id              The id of the recipe
     * @param needPlaceholder Whether this recipe need a vanilla placeholder (that's to say, if there has already been a vanilla recipe sharing the same form with this recipe, set this to false, otherwise, set this to true)
     */
    public static void register(RecipeBase recipeBase, String id, boolean needPlaceholder) {
        recipes.add(recipeBase);
        if (needPlaceholder)
            if (recipeBase instanceof SmeltingRecipe) {
                SmeltingRecipe smelting = (SmeltingRecipe) recipeBase;
                Bukkit.addRecipe(new FurnaceRecipe(new NamespacedKey(WheelCore.instance, "furnace_recipe_" + id),
                                                   recipeBase.getResult(),
                                                   new RecipeChoice.ExactChoice(smelting.getAllMatches()),
                                                   smelting.getExperience(),
                                                   smelting.getCookingTime()));
            } else if (recipeBase instanceof ShapedRecipe) {
                ShapedRecipe shaped = (ShapedRecipe) recipeBase;
                org.bukkit.inventory.ShapedRecipe recipe = new org.bukkit.inventory.ShapedRecipe(new NamespacedKey(
                        WheelCore.instance,
                        "shaped_recipe_" + id), recipeBase.getResult()).shape("abc", "def", "ghi");
                setShapedIfExist(recipe, shaped, 'a', 0);
                setShapedIfExist(recipe, shaped, 'b', 1);
                setShapedIfExist(recipe, shaped, 'c', 2);
                setShapedIfExist(recipe, shaped, 'd', 3);
                setShapedIfExist(recipe, shaped, 'e', 4);
                setShapedIfExist(recipe, shaped, 'f', 5);
                setShapedIfExist(recipe, shaped, 'g', 6);
                setShapedIfExist(recipe, shaped, 'h', 7);
                setShapedIfExist(recipe, shaped, 'i', 8);
                placeholders.add(recipe.getKey());
                Bukkit.addRecipe(recipe);
            } else if (recipeBase instanceof ShapelessRecipe) {
                ShapelessRecipe shapeless = (ShapelessRecipe) recipeBase;
                org.bukkit.inventory.ShapelessRecipe recipe = new org.bukkit.inventory.ShapelessRecipe(new NamespacedKey(
                        WheelCore.instance,
                        "shapeless_recipe_" + id), recipeBase.getResult());
                shapeless.getChoices().forEach(recipe::addIngredient);
                placeholders.add(recipe.getKey());
                Bukkit.addRecipe(recipe);
            }
    }

    public static void register(SmeltingRecipe recipe, String id) {
        register(recipe, id, true);
    }

    public static void register(ShapedRecipe recipe, String id) {
        register(recipe, id, true);
    }

    public static void register(ShapelessRecipe recipe, String id) {
        register(recipe, id, true);
    }

    public static void register(RecipeBase recipe, String id) {
        register(recipe, id, false);
    }

    private static void setShapedIfExist(org.bukkit.inventory.ShapedRecipe recipe, ShapedRecipe shaped, char key, int slot) {
        RecipeChoice.MaterialChoice choice = shaped.getChoiceAt(slot);
        if (choice != null)
            recipe.setIngredient(key, choice);
    }

    public static boolean isPlaceholder(Recipe recipe) {
        if (recipe instanceof org.bukkit.inventory.ShapedRecipe)
            return placeholders.contains(((org.bukkit.inventory.ShapedRecipe) recipe).getKey());
        else if (recipe instanceof org.bukkit.inventory.ShapelessRecipe)
            return placeholders.contains(((org.bukkit.inventory.ShapelessRecipe) recipe).getKey());
        else
            return false;
    }

    public static CraftingRecipe matchCraftingRecipe(@Nonnull List<ItemStack> items, @Nullable Map<Integer, ItemStack> damageResult) {
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
            if (recipeBase instanceof CraftingRecipe && ((CraftingRecipe) recipeBase).matches(matrix, damageResult)) {
                return (CraftingRecipe) recipeBase;
            }
        }

        return null;
    }

    public static SmeltingRecipe matchSmeltingRecipe(@Nonnull ItemStack origin) {
        for (RecipeBase recipeBase : recipes) {
            if (recipeBase instanceof SmeltingRecipe && ((SmeltingRecipe) recipeBase).matches(origin)) {
                return (SmeltingRecipe) recipeBase;
            }
        }
        return null;
    }

    public static <T extends SingleInputRecipe> T matchSpecificRecipe(@Nonnull Class<T> recipeType, @Nonnull ItemStack origin) {
        for (RecipeBase recipeBase : recipes) {
            if (recipeType.isInstance(recipeBase) && ((SingleInputRecipe) recipeBase).matches(origin)) {
                return (T) recipeBase;
            }
        }
        return null;
    }

    public static <T extends MatrixInputRecipe> T matchSpecificRecipe(@Nonnull Class<T> recipeType, @Nonnull List<List<ItemStack>> matrix) {
        for (RecipeBase recipeBase : recipes) {
            if (recipeType.isInstance(recipeBase) && ((MatrixInputRecipe) recipeBase).matches(matrix)) {
                return (T) recipeBase;
            }
        }
        return null;
    }
}
