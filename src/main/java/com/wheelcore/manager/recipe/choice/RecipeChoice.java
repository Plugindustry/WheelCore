package com.wheelcore.manager.recipe.choice;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

public interface RecipeChoice {
    boolean matches(ItemStack item);

    MaterialChoice getPlaceholderChoice();
}
