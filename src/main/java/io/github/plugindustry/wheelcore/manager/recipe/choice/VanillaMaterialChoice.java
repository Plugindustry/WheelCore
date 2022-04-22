package io.github.plugindustry.wheelcore.manager.recipe.choice;

import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VanillaMaterialChoice implements RecipeChoice {
    private final Set<Material> materials;

    public VanillaMaterialChoice(Set<Material> materials) {
        this.materials = new HashSet<>(materials);
    }

    public VanillaMaterialChoice(Material... materials) {
        this.materials = new HashSet<>(Arrays.asList(materials));
    }

    @Override
    public boolean matches(@Nonnull ItemStack item) {
        return MainManager.getItemInstance(item) == null && materials.contains(item.getType());
    }

    @Nonnull
    @Override
    public org.bukkit.inventory.RecipeChoice.MaterialChoice getPlaceholderChoice() {
        return new org.bukkit.inventory.RecipeChoice.MaterialChoice(materials.toArray(Material[]::new));
    }
}