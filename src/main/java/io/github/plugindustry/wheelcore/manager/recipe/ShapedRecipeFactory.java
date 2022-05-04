package io.github.plugindustry.wheelcore.manager.recipe;

import io.github.plugindustry.wheelcore.manager.recipe.choice.ItemStackChoice;
import io.github.plugindustry.wheelcore.manager.recipe.choice.RecipeChoice;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShapedRecipeFactory {
    private final Map<Character, RecipeChoice> patternMapping = new HashMap<>();
    private final Map<RecipeChoice, Integer> damageMapping = new HashMap<>();
    private List<String> patterns = new LinkedList<>();

    public static ShapedRecipeFactory create() {
        return new ShapedRecipeFactory();
    }

    public ShapedRecipeFactory map(char c, ItemStack target) {
        return map(c, new ItemStackChoice(target));
    }

    public ShapedRecipeFactory map(char c, RecipeChoice target) {
        this.patternMapping.put(c, target);
        return this;
    }

    public ShapedRecipeFactory pattern(String... patterns) {
        List<String> lastPattern = new LinkedList<>(Arrays.asList(patterns));

        if (patterns.length > 3) {
            throw new IllegalArgumentException("long pattern");
        }
        this.patterns.clear();

        for (String s : patterns) {
            if (s.length() > 3) {
                this.patterns = lastPattern;
                throw new IllegalArgumentException("long pattern");
            }

            this.patterns.add(s);
        }

        return this;
    }

    public ShapedRecipeFactory addDamage(ItemStack itemStack, int damage) {
        return addDamage(new ItemStackChoice(itemStack), damage);
    }

    public ShapedRecipeFactory addDamage(RecipeChoice choice, int damage) {
        this.damageMapping.put(choice, damage);
        return this;
    }

    public ShapedRecipe build(ItemStack result) {
        List<List<RecipeChoice>> recipe = new LinkedList<>();

        for (String pattern : patterns) {
            List<RecipeChoice> current = new LinkedList<>();
            recipe.add(current);

            for (char c : pattern.toCharArray()) {
                current.add(patternMapping.getOrDefault(c, null));
            }
        }

        ShapedRecipe craftingRecipe = new ShapedRecipe(recipe, result.clone());

        this.damageMapping.forEach(craftingRecipe::addItemCost);
        return craftingRecipe;
    }
}