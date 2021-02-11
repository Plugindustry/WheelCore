package com.wheelcore.manager.recipe;

import com.wheelcore.manager.recipe.choice.RecipeChoice;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface CraftingRecipe extends RecipeBase {
    boolean matches(List<List<ItemStack>> recipe, @Nullable Map<Integer, ItemStack> damageResult);

    CraftingRecipe addItemCost(ItemStack is, int durability);

    CraftingRecipe addItemCost(RecipeChoice choice, int durability);
}
