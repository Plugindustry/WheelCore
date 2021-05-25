package io.github.plugindustry.wheelcore.manager.recipe.choice;

import io.github.plugindustry.wheelcore.manager.ItemMapping;
import io.github.plugindustry.wheelcore.utils.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class OreDictionaryChoice implements RecipeChoice {
    private final HashSet<String> dictionaryKeys = new HashSet<>();
    private boolean exact = true;

    public OreDictionaryChoice(String... dictionaryKeys) {
        this(Arrays.asList(dictionaryKeys));
    }

    public OreDictionaryChoice(List<String> dictionaryKeys) {
        this.dictionaryKeys.addAll(dictionaryKeys);
    }

    public OreDictionaryChoice(List<String> dictionaryKeys, boolean exact) {
        this(dictionaryKeys);
        this.exact = exact;
    }

    @Override
    public boolean matches(ItemStack item) {
        return ((!exact) || ItemStackUtil.getDurability(item) == 0) &&
               ItemStackUtil.getOreDictionary(item).stream().anyMatch(dictionaryKeys::contains);
    }

    @Override
    public org.bukkit.inventory.RecipeChoice.MaterialChoice getPlaceholderChoice() {
        return new org.bukkit.inventory.RecipeChoice.MaterialChoice(dictionaryKeys.stream()
                                                                            .map(ItemMapping.dictMaterial::get)
                                                                            .flatMap(Collection::stream)
                                                                            .distinct()
                                                                            .collect(Collectors.toList()));
    }
}
