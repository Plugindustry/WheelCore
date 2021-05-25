package io.github.plugindustry.wheelcore.manager.recipe;

import io.github.plugindustry.wheelcore.manager.recipe.choice.ItemStackChoice;
import io.github.plugindustry.wheelcore.manager.recipe.choice.RecipeChoice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShapelessRecipe implements CraftingRecipe {
    private final ArrayList<RecipeChoice> recipe;
    private final ItemStack result;
    private final Map<RecipeChoice, Integer> damages = new HashMap<>();

    public ShapelessRecipe(ItemStack result, ItemStack... items) {
        this(Stream.of(items)
                     .filter(item -> item != null && item.getType() != Material.AIR)
                     .map(ItemStackChoice::new)
                     .collect(Collectors.toList()), result);
    }

    public ShapelessRecipe(List<RecipeChoice> choices, ItemStack result) {
        this.recipe = new ArrayList<>(choices);
        // remove the airs
        this.recipe.removeIf(Objects::isNull);
        this.result = result;
    }

    @Override
    public CraftingRecipe addItemCost(RecipeChoice choice, int durability) {
        this.damages.put(choice, durability);
        return this;
    }

    @Override
    public boolean matches(@Nonnull List<List<ItemStack>> recipe, @Nullable Map<Integer, ItemStack> damage) {
        List<ItemStack> shapeless = new LinkedList<>();
        List<RecipeChoice> checkList = new LinkedList<>(this.recipe);
        // convert everything to shapeless
        recipe.forEach(shapeless::addAll);

        for (Iterator<ItemStack> origin = shapeless.iterator(); origin.hasNext(); /*lol*/) {
            ItemStack is = origin.next();
            if (is == null || is.getType() == Material.AIR) {
                origin.remove();
                continue;
            }
            for (Iterator<RecipeChoice> check = checkList.iterator(); check.hasNext(); /*qwq*/) {
                if (check.next().matches(is)) {
                    check.remove();
                    origin.remove();
                }
            }
        }

        boolean result = checkList.isEmpty() && shapeless.isEmpty();
        if (result && damage != null) {
            // check for damage to items.
            ShapedRecipe.checkItemDamage(recipe, damage, this.damages);
        }

        return true;
    }

    public List<org.bukkit.inventory.RecipeChoice.MaterialChoice> getChoices() {
        return recipe.stream().map(RecipeChoice::getPlaceholderChoice).collect(Collectors.toList());
    }

    @Override
    public ItemStack getResult() {
        return result.clone();
    }
}
