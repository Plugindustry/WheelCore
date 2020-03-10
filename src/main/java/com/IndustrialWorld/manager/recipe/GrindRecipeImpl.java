package com.IndustrialWorld.manager.recipe;

import org.bukkit.inventory.ItemStack;

public class GrindRecipeImpl implements GrindRecipe {
	private double power;
	private ItemStack recipe;
	private ItemStack result;

	public GrindRecipeImpl(double power, ItemStack recipe, ItemStack result) {
		this.power = power;
		this.recipe = recipe;
		this.result = result;
	}

	@Override
	public boolean matches(ItemStack itemStack) {
		return recipe.isSimilar(itemStack);
	}

	@Override
	public double getPowerNeeded() {
		return power;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}
}
