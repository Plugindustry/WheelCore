package com.IndustrialWorld.manager.recipe;

import com.IndustrialWorld.utils.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapedRecipe implements CraftingRecipe {
	private List<List<ItemStack>> matrix;
	private ItemStack result;
	private Map<ItemStack, Integer> damages = new HashMap<>();

	protected ShapedRecipe(List<List<ItemStack>> matrix, ItemStack result) {
		if (matrix.size() <= 0 || matrix.size() > 3 || matrix.get(0).size() <= 0 || matrix.get(0).size() > 3) {
			throw new IllegalArgumentException("Incorrect size of recipe");
		}
		this.matrix = matrix;

		this.result = result;
	}

	@Override
	public boolean matches(List<List<ItemStack>> matrix, Map<Integer, ItemStack> damage) {
		if (matrix.size() != 3) {
			return false;
		}

		for (int i = 0; i < matrix.size(); i++) {
			List<ItemStack> row = matrix.get(i);
			for (int j = 0; j < row.size(); j++) {
				ItemStack is = row.get(j);
                ItemStack temp;
                if (is == null)
                    temp = null;
                else {
                    temp = is.clone();
                    if (temp.getType().getMaxDurability() != 0)
                        temp.setDurability((short) 0);
                }

                if (!ItemStackUtil.isSimilar(this.matrix.get(i).get(j), temp)) {
					return false;
				}
			}
		}

		// check for damage to items.
		checkItemDamage(matrix, damage, this.damages);

		return true;
	}

	static void checkItemDamage(List<List<ItemStack>> matrix, Map<Integer, ItemStack> damage, Map<ItemStack, Integer> damages) {
        for (int i = 0; i < matrix.size(); ++i) {
			List<ItemStack> row = matrix.get(i);
            for (int j = 0; j < row.size(); ++j) {
				ItemStack is = row.get(j);
                final int finalI = i;
                final int finalJ = j;
				damages.forEach((items, dmg) -> {
                    ItemStack temp;
                    if (is == null)
                        temp = null;
                    else {
                        temp = is.clone();
                        temp.setDurability((short) 0);
                    }

                    if (ItemStackUtil.isSimilar(items, temp)) {
						ItemStack newIs = is.clone();
                        newIs.setDurability((short) (newIs.getDurability() + dmg));
						if (damage != null) {
                            damage.put(finalI * 3 + finalJ + 1, newIs);
						}
					}
				});
			}
		}
	}

	@Override
	public CraftingRecipe addItemCost(ItemStack is, int durability) {
		this.damages.put(is, durability);
		return this;
	}

	@Override
	public ItemStack getResult() {
		return result.clone();
	}
}
