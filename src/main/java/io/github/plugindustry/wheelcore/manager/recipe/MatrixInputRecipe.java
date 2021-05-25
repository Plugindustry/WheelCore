package io.github.plugindustry.wheelcore.manager.recipe;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public interface MatrixInputRecipe extends RecipeBase {
    boolean matches(@Nonnull List<List<ItemStack>> matrix);
}
