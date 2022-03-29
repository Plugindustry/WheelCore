package io.github.plugindustry.wheelcore.manager.recipe.choice;

import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemInstanceChoice implements RecipeChoice {
    private final HashSet<ItemBase> instances;

    public ItemInstanceChoice(ItemBase... items) {
        instances = new HashSet<>();
        Collections.addAll(instances, items);
    }

    public ItemInstanceChoice(Set<ItemBase> items) {
        instances = new HashSet<>(items);
    }

    @Override
    public boolean matches(@Nonnull ItemStack item) {
        return instances.contains(MainManager.getItemInstance(item));
    }

    @Nonnull
    @Override
    public org.bukkit.inventory.RecipeChoice.MaterialChoice getPlaceholderChoice() {
        return new org.bukkit.inventory.RecipeChoice.MaterialChoice(instances.stream()
                                                                             .map(ItemBase::getMaterial)
                                                                             .collect(Collectors.toList()));
    }
}
