package com.IndustrialWorld.manager.recipe;

import com.IndustrialWorld.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShapedRecipe implements CraftingRecipe {
	private List<List<ItemStack>> matrix;
	private ItemStack result;
	private Map<ItemStack, Integer> damages = new HashMap<>();

	public ShapedRecipe(List<List<ItemStack>> matrix, ItemStack result) {
		if (matrix.size() <= 0 || matrix.size() > 3 || matrix.get(0).size() <= 0 || matrix.get(0).size() > 3) {
			throw new IllegalArgumentException("Incorrect size of recipe");
		}
		// optimize the matrix.
		optimizeMatrix(matrix);
		if (matrix.isEmpty()) {
			throw new IllegalArgumentException("Null recipe.");
		}
		this.matrix = matrix;

		this.result = result;
	}

	@Override
	public boolean matches(List<List<ItemStack>> matrix, Map<Integer, ItemStack> damage) {
		if (matrix.size() != 9) {
			return false;
		}

		// optimize the whole matrix
		optimizeMatrix(matrix);

		for (int i = 0; i < matrix.size(); i++) {
			List<ItemStack> row = matrix.get(i);
			for (int j = 0; j < row.size(); j++) {
				ItemStack is = row.get(j);

				if (!ItemStackUtil.isSimilar(this.matrix.get(i).get(j), is)) {
					return false;
				}
			}
		}

		// check for damage to items.
		for (int i = 0; i < matrix.size(); i++) {
			List<ItemStack> row = matrix.get(i);
			for (int j = 0; j < row.size(); j++) {
				ItemStack is = row.get(j);
				int finalI = i;
				int finalJ = j;
				this.damages.forEach((items, dmg) -> {
					if (ItemStackUtil.isSimilar(items, is)) {
						ItemStack newIs = is.clone();
						newIs.setDurability((short) (newIs.getDurability() - dmg));
						if (damage != null) {
							damage.put(finalI * 3 + finalJ, newIs);
						}
					}
				});
			}
		}

		return true;
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

	private void optimizeMatrix(List<List<ItemStack>> matrix) {
		int startAirLength = Integer.MAX_VALUE;
		// search the thinnest air the rows start with.
		for (List<ItemStack> row : matrix) {
			int airLength = 0;
			for (ItemStack is : row) {
				if (is == null || is.getType() == Material.AIR) {
					airLength ++;
				} else {
					break;
				}
			}

			if (startAirLength > airLength) {
				startAirLength = airLength;
			}
		}

		for (int i = 0; i < startAirLength; i ++) {
			for (List<ItemStack> row : matrix) {
				try {
					row.remove(0);
				} catch (IndexOutOfBoundsException e) {
					// oops, it is already optimized
				}
			}
		}

		// then search the thinnest air the rows end with
		int endAirLength = Integer.MAX_VALUE;
		for (List<ItemStack> row : matrix) {
			int airLength = 0;
			for (int i = 2; i >= 0; i --) {
				ItemStack is = row.get(i);

				if (is == null || is.getType() == Material.AIR) {
					airLength ++;
				} else {
					break;
				}
			}

			if (endAirLength > airLength) {
				endAirLength = airLength;
			}
		}

		for (int i = 0; i < endAirLength; i ++) {
			for (List<ItemStack> row : matrix) {
				try {
					row.remove(2 - i);
				} catch (IndexOutOfBoundsException e) {
					// it is optimized too...
				}
			}
		}

		// check the empty things to remove.
		List<Integer> removeQueue = new LinkedList<>();

		for (int i = 0; i <= 2; i ++) {
			List<ItemStack> row = matrix.get(i);

			if (row.isEmpty()) {
				removeQueue.add(i);
				continue;
			}

			boolean queued = true;
			for (ItemStack is : row) {
				if (is != null && is.getType() != Material.AIR) {
					queued = false;
				}
			}

			if (queued) {
				removeQueue.add(i);
				continue;
			}

			// We do break if we found a non-empty row, in order to keep everything safe.
			break;
		}

		// reversed so that we can remove the rows completely
		for (int i = 2; i >= 0; i --) {
			List<ItemStack> row = matrix.get(i);

			if (row.isEmpty()) {
				removeQueue.add(i);
				continue;
			}

			boolean queued = true;
			for (ItemStack is : row) {
				if (is != null && is.getType() != Material.AIR) {
					queued = false;
				}
			}

			if (queued) {
				removeQueue.add(i);
				continue;
			}

			// We do break if we found a non-empty row, in order to keep everything safe.
			break;
		}

		for (Integer i : removeQueue) {
			matrix.remove(i.intValue());
		}
	}
}
