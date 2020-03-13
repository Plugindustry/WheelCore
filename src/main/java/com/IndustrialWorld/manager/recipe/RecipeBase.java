package com.IndustrialWorld.manager.recipe;

import com.IndustrialWorld.item.material.IWMaterial;
import org.bukkit.inventory.ItemStack;

public interface RecipeBase {
	ItemStack getResult(IWMaterial iwMaterial);

	class MatchInfo {
		private boolean matches;
		private boolean hasIWMaterial;
		private IWMaterial iwMaterial;

		public MatchInfo(boolean matches, boolean hasIWMaterial, IWMaterial iwMaterial) {
			this.matches = matches;
			this.hasIWMaterial = hasIWMaterial;
			this.iwMaterial = iwMaterial;
		}

		public IWMaterial getIwMaterial() {
			return iwMaterial;
		}

		public boolean isHasIWMaterial() {
			return hasIWMaterial;
		}

		public boolean isMatches() {
			return matches;
		}
	}

	class RecipeResultInfo {
		private CraftingRecipe recipe;
		private IWMaterial iwMaterial;

		public RecipeResultInfo(CraftingRecipe recipe, IWMaterial iwMaterial) {
			this.recipe = recipe;
			this.iwMaterial = iwMaterial;
		}

		public CraftingRecipe getRecipe() {
			return recipe;
		}

		public IWMaterial getIwMaterial() {
			return iwMaterial;
		}
	}
}
