package com.industrialworld.manager.recipe;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface CraftingRecipe extends RecipeBase {
    ShapedRecipe.MatchInfo matches(List<List<ItemStack>> recipe, @Nullable Map<Integer, ItemStack> damageResult);

    CraftingRecipe addItemCost(ItemStack is, int durability);
}
