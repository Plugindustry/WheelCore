package com.IndustrialWorld.manager.recipe;

import org.bukkit.inventory.ItemStack;

public class SmeltingRecipeImpl implements SmeltingRecipe {
	private ItemStack recipe;
	private ItemStack result;

	public SmeltingRecipeImpl(ItemStack recipe, ItemStack result) {
		this.recipe = recipe;
		this.result = result;
	}

	@Override
	public boolean matches(ItemStack origin) {
		if (!origin.isSimilar(recipe)) {
			return false;
		}
		return true;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}
}
