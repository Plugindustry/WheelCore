package com.IndustrialWorld.manager.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class ShapedRecipeTest {
	private static final ItemStack owo1 = new ItemStack(Material.IRON_AXE);
	private static final ItemStack owo2 = new ItemStack(Material.GOLD_ORE);
	private static final ItemStack owo3 = new ItemStack(Material.GOLD_INGOT);
	private static final ItemStack air = new ItemStack(Material.AIR);

	@org.junit.jupiter.api.Test
	void matches() {
		List<List<ItemStack>> matrix = new LinkedList<>();
		matrix.add(Arrays.asList(owo1, owo2, owo2));
		matrix.add(Arrays.asList(air, air, air));
		matrix.add(Arrays.asList(air, air, air));

		List<List<ItemStack>> copy = new LinkedList<>(matrix);

		ShapedRecipe sr = new ShapedRecipe(copy, owo3);

		Assertions.assertTrue(sr.matches(matrix, null));
	}
}