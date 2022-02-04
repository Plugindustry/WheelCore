package io.github.plugindustry.wheelcore.manager.recipe.choice;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import javax.annotation.Nonnull;

public interface RecipeChoice {
    boolean matches(@Nonnull ItemStack item);

    @Nonnull
    MaterialChoice getPlaceholderChoice();
}
