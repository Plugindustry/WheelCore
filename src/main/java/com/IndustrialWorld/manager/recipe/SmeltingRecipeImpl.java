package com.IndustrialWorld.manager.recipe;

import org.bukkit.inventory.ItemStack;

public class SmeltingRecipeImpl implements SmeltingRecipe {
	private ItemStack recipe;
	private ItemStack fuel;
	private ItemStack result;

	public SmeltingRecipeImpl(ItemStack recipe, ItemStack result) {
		this(recipe, null, result);
	}

	public SmeltingRecipeImpl(ItemStack recipe, ItemStack fuel, ItemStack result) {
		this.recipe = recipe;
		this.fuel = fuel;
		this.result = result;
	}

	@Override
	public boolean matches(ItemStack origin, ItemStack fuel) {
		if (!origin.isSimilar(recipe)) {
			return false;
		}
		return fuel == null || this.fuel.isSimilar(fuel);
	}

	@Override
	public ItemStack getResult() {
		return result;
	}
}
