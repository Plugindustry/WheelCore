package com.IndustrialWorld.item;

import org.bukkit.inventory.ItemStack;

public class IndustrialItem {
	private String id;
	private ItemStack standardItemStack;

	public IndustrialItem(String id, ItemStack standardItemStack) {
		this.id = id;
		this.standardItemStack = standardItemStack;
	}
}
