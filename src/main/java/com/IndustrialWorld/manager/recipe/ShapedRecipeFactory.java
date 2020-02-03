package com.IndustrialWorld.manager.recipe;

import com.IndustrialWorld.utils.DebuggingLogger;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShapedRecipeFactory {
	private List<String> patterns = new LinkedList<>();
	private Map<Character, ItemStack> patternMapping = new HashMap<>();
	private Map<ItemStack, Integer> damageMapping = new HashMap<>();

	public ShapedRecipeFactory map(char c, ItemStack target) {
		this.patternMapping.put(c, target.clone());
		return this;
	}

	public ShapedRecipeFactory pattern(String ... patterns) {
		List<String> lastPattern = new LinkedList<>();
		for (String pattern : patterns) {
			DebuggingLogger.debug("Adding pattern: " + pattern);
			lastPattern.add(pattern);
		}

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
		this.damageMapping.put(itemStack, damage);
		return this;
	}

	public RecipeBase build(ItemStack result) {
		List<List<ItemStack>> recipe = new LinkedList<>();

		for (String pattern : patterns) {
			List<ItemStack> current = new LinkedList<>();
			recipe.add(current);

			for (char c : pattern.toCharArray()) {
				current.add(patternMapping.getOrDefault(c, null));
			}
		}

		CraftingRecipe craftingRecipe = new ShapedRecipe(recipe, result.clone());

		this.damageMapping.forEach(craftingRecipe::addItemCost);
		DebuggingLogger.debug(craftingRecipe.toString());
		return craftingRecipe;
	}
}
