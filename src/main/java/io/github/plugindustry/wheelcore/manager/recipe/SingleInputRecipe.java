package io.github.plugindustry.wheelcore.manager.recipe;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface SingleInputRecipe extends RecipeBase {
    boolean matches(@Nonnull ItemStack origin);
}
