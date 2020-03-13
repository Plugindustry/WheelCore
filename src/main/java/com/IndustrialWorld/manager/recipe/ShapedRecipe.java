package com.IndustrialWorld.manager.recipe;

import com.IndustrialWorld.item.ItemType;
import com.IndustrialWorld.item.material.IWMaterial;
import com.IndustrialWorld.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapedRecipe implements CraftingRecipe {
	private List<List<Object>> matrix;
	private Object result;
	private Map<ItemStack, Integer> damages = new HashMap<>();

	protected ShapedRecipe(List<List<Object>> matrix, Object result) {
		if (matrix.size() <= 0 || matrix.size() > 3 || matrix.get(0).size() <= 0 || matrix.get(0).size() > 3) {
			throw new IllegalArgumentException("Incorrect size of recipe");
		}
		this.matrix = matrix;

		this.result = result;
	}

	@Override
	public MatchInfo matches(List<List<ItemStack>> matrix, Map<Integer, ItemStack> damage) {
		if (matrix.size() != 3) {
			return new MatchInfo(false, false, null);
		}

		IWMaterial material = null;

		for (int i = 0; i < matrix.size(); i++) {
			List<ItemStack> row = matrix.get(i);
			for (int j = 0; j < row.size(); j++) {
				if (this.matrix.get(i).get(j) instanceof ItemStack) {
					ItemStack is = row.get(j);
					ItemStack temp;
					if (is == null)
						temp = null;
					else {
						temp = is.clone();
						if (temp.getType().getMaxDurability() != 0)
							temp.setDurability((short) 0);
					}

					if (!ItemStackUtil.isSimilar((ItemStack) this.matrix.get(i).get(j), temp)) {
						return new MatchInfo(false, false, null);
					}
				} else if (this.matrix.get(i).get(j) instanceof ItemType) {
					IWMaterial currentMaterial = ItemStackUtil.getItemMaterial(row.get(j));
					if (material == null || material.equals(currentMaterial)) {
						material = currentMaterial;
					} else {
						return new MatchInfo(false, false, null);
					}
					if (!ItemStackUtil.getItemType(row.get(j)).equals(this.matrix.get(i).get(j))) {
						return new MatchInfo(false, false, null);
					}
				} else {
					throw new IllegalArgumentException("The object in matrix is neither ItemStack nor ItemType.");
				}
			}
		}

		// check for damage to items.
		checkItemDamage(matrix, damage, this.damages);

		if (material == null) {
			return new MatchInfo(true, false, null);
		}
		return new MatchInfo(true, true, material);
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
	public ItemStack getResult(IWMaterial iwMaterial) {
		if (result instanceof ItemStack) {
			return ((ItemStack) result).clone();
		} else if (result instanceof ItemType) {
			try {
				return (ItemStack) ((ItemType) result).getTemplateClass().getMethod("getItemStack", IWMaterial.class).invoke(((ItemType) result).getTemplateClass(), iwMaterial);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		} else {
			return new ItemStack(Material.AIR);
		}
		return new ItemStack(Material.AIR);
	}
}
