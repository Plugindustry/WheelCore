package com.IndustrialWorld.manager.recipe;

import com.IndustrialWorld.item.material.IWMaterial;
import com.IndustrialWorld.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShapelessRecipe implements CraftingRecipe {
	private List<ItemStack> recipe;
	private ItemStack result;
	private Map<ItemStack, Integer> damages = new HashMap<>();

	public ShapelessRecipe(List<ItemStack> items, ItemStack result) {
		// remove the airs
		items.removeIf(item -> item == null || item.getType() == Material.AIR);
		this.recipe = items;
		this.result = result;
	}

	@Override
	public CraftingRecipe addItemCost(ItemStack is, int durability) {
		this.damages.put(is, durability);
		return this;
	}

	@Override
	public MatchInfo matches(List<List<ItemStack>> recipe, Map<Integer, ItemStack> damage) {
		List<ItemStack> shapeless = new LinkedList<>();
		List<ItemStack> checkList = new LinkedList<>(this.recipe);
		// convert everything to shapeless
		for (List<ItemStack> row : recipe) {
			for (ItemStack is : row) {
				if (is != null && is.getType() != Material.AIR) {
					shapeless.add(is.clone());
				}
			}
		}

		for (Iterator<ItemStack> origin = shapeless.iterator(); origin.hasNext(); /*lol*/) {
			ItemStack is = origin.next();
            ItemStack temp = is.clone();
            if (temp.getType().getMaxDurability() != 0)
                temp.setDurability((short) 0);
			for (Iterator<ItemStack> check = checkList.iterator(); check.hasNext(); /*qwq*/) {
                if (ItemStackUtil.isSimilar(check.next(), temp)) {
					check.remove();
					origin.remove();
				}
			}
		}

		boolean result = checkList.isEmpty() && shapeless.isEmpty();
		if (result) {
			// check for damage to items.
			ShapedRecipe.checkItemDamage(recipe, damage, this.damages);
		}

		return new MatchInfo(result, false, null);
	}

	@Override
	public ItemStack getResult(IWMaterial iwMaterial) {
		return result.clone();
	}
}
