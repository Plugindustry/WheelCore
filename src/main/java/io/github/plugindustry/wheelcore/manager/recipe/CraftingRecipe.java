package io.github.plugindustry.wheelcore.manager.recipe;

import io.github.plugindustry.wheelcore.manager.recipe.choice.RecipeChoice;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface CraftingRecipe extends RecipeBase {
    boolean matches(@Nonnull List<List<ItemStack>> recipe, @Nullable Map<Integer, ItemStack> damageResult);

    CraftingRecipe addItemCost(RecipeChoice choice, int durability);
}
