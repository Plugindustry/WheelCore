package io.github.plugindustry.wheelcore.manager.recipe;

import io.github.plugindustry.wheelcore.manager.recipe.choice.ItemStackChoice;
import io.github.plugindustry.wheelcore.manager.recipe.choice.RecipeChoice;
import io.github.plugindustry.wheelcore.utils.BipartiteGraph;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShapelessRecipe implements CraftingRecipe {
    private final ArrayList<RecipeChoice> recipe;
    private final ItemStack result;
    private final Map<RecipeChoice, Integer> damages = new HashMap<>();

    public ShapelessRecipe(ItemStack result, ItemStack... items) {
        this(Stream.of(items).filter(item -> item != null && item.getType() != Material.AIR).map(ItemStackChoice::new)
                .collect(Collectors.toList()), result);
    }

    public ShapelessRecipe(List<RecipeChoice> choices, ItemStack result) {
        this.recipe = new ArrayList<>(choices);
        // remove the airs
        this.recipe.removeIf(Objects::isNull);
        this.result = result;
    }

    @Override
    public ShapelessRecipe addItemCost(RecipeChoice choice, int durability) {
        this.damages.put(choice, durability);
        return this;
    }

    @Override
    public boolean matches(@Nullable Player player, @Nonnull List<List<ItemStack>> matrix,
            @Nullable Map<Integer, ItemStack> damage) {
        List<ItemStack> shapeless = new ArrayList<>();
        matrix.forEach(shapeless::addAll);

        if (shapeless.size() != recipe.size()) return false;

        int size = recipe.size();
        BipartiteGraph graph = new BipartiteGraph(size, size);

        for (int i = 0; i < size; ++i) {
            RecipeChoice choice = recipe.get(i);
            boolean flag = false;
            for (int j = 0; j < size; ++j) {
                ItemStack item = shapeless.get(j);
                if (choice.matches(item)) {
                    flag = true;
                    graph.addEdge(i, size + j);
                }
            }
            if (!flag) return false;
        }

        boolean result = graph.maxMatch() == size;
        if (result && damage != null) ShapedRecipe.checkItemDamage(player, matrix, damage, this.damages);

        return result;
    }

    public List<org.bukkit.inventory.RecipeChoice.MaterialChoice> getChoices() {
        return recipe.stream().map(RecipeChoice::getPlaceholderChoice).collect(Collectors.toList());
    }

    @Override
    public ItemStack getResult() {
        return result.clone();
    }
}