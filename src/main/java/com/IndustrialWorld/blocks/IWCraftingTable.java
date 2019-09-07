package com.IndustrialWorld.blocks;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.event.TickEvent;
import com.IndustrialWorld.i18n.I18n;
import com.IndustrialWorld.i18n.I18nConst;
import com.IndustrialWorld.interfaces.BlockBase;
import com.IndustrialWorld.interfaces.InventoryListener;
import com.IndustrialWorld.manager.RecipeRegistry;
import com.IndustrialWorld.manager.recipe.CraftingRecipe;
import com.IndustrialWorld.manager.recipe.RecipeBase;
import com.IndustrialWorld.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.*;

public class IWCraftingTable extends BlockBase implements InventoryListener {
	private static ArrayList<Recipe> recipes = new ArrayList<>();
    private List<Inventory> availableInventories = new ArrayList<>();
    private Map<Inventory, RecipeBase> lastRecipe = new HashMap<>();

	@Override
	public ItemStack getItemStack() {
		return ConstItems.IW_CRAFTING_TABLE;
	}

	@Override
	public String getId() {
		return "IWCraftingTable";
	}

	@Override
	public Material getMaterial() {
		return ConstItems.IW_CRAFTING_TABLE.getType();
	}

	@Override
	public boolean onInteract(Player player, Action action, ItemStack tool, Block block) {
		if (action == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) {
			Inventory i = Bukkit.createInventory(null, InventoryType.WORKBENCH, I18n.getLocaleString(I18nConst.Inventory.IW_CRAFTING_TABLE_TITLE));
			player.openInventory(i);
			availableInventories.add(i);

			return false;
		}

		return true;
	}

	private List<ItemStack> fetchMatrix(Inventory inventory) {
		ItemStack[] raw = inventory.getStorageContents();
		// fetch recipe that matches
		ItemStack[] rawMatrix = new ItemStack[9];
		System.arraycopy(raw, 1, rawMatrix, 0, 9);
		return Arrays.asList(rawMatrix);
	}

	@Override
    public void onTick(TickEvent event) {
		// Use getStorageContents & setStorageContents to control the crafting table. The result will be put on the slot 0
		for (Inventory craftingInv : availableInventories) {
			ItemStack[] raw = craftingInv.getStorageContents();
			RecipeBase recipe = RecipeRegistry.matchCraftingRecipe(fetchMatrix(craftingInv), null);

			if (recipe == null) {
				// invalid recipe
				continue;
			}
			raw[0] = recipe.getResult();
			craftingInv.setStorageContents(raw);

			if (lastRecipe.get(craftingInv) != recipe) {
				for (HumanEntity p : craftingInv.getViewers()) {
					InventoryUtil.updateInventoryWithoutCarriedItem((Player) p);
				}
				lastRecipe.put(craftingInv, recipe);
			}
		}
    }

	public static void registerRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public boolean isInventoryAvailable(Inventory ci) {
        return availableInventories.contains(ci);
    }

	@Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (isInventoryAvailable(event.getClickedInventory()) && event.getSlot() == 0) {
            InventoryUtil.updateInventoryWithoutCarriedItem((Player) event.getClickedInventory().getViewers().get(0));
            if (!(event.getAction() == InventoryAction.PICKUP_ALL ||
                  ((event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_ALL) &&
                   event.getClickedInventory().getStorageContents()[0] != null))) {
                event.setCancelled(true);
                return;
            }

            Inventory craftInv = event.getClickedInventory();

            Map<Integer, ItemStack> damagedItemIndex = new HashMap<>();
            CraftingRecipe recipe = RecipeRegistry.matchCraftingRecipe(fetchMatrix(event.getClickedInventory()), damagedItemIndex);
            if (recipe == null) {
	            return;
            }

            ItemStack[] content = craftInv.getStorageContents();
            for (int i = 1; i <= 9; i ++) {
            	if (damagedItemIndex.containsKey(i)) {
            		content[i] = damagedItemIndex.get(i);
            		continue;
	            }
            	if (content[i] == null) {
            		continue;
	            }
            	content[i].setAmount(content[i].getAmount() - 1);
            }
            craftInv.setStorageContents(content);

            InventoryUtil.updateInventoryWithoutCarriedItem((Player) event.getClickedInventory().getViewers().get(0));
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        if (isInventoryAvailable(event.getInventory())) {
            ItemStack[] buf = event.getInventory().getStorageContents();
            buf[0] = new ItemStack(Material.AIR);
            for (ItemStack is : buf)
                if (is != null && is.getType() != Material.AIR)
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), is);
        }
    }

    public static class IWShapedRecipe implements Recipe {
        private String shape;
        private ItemStack[] stacks = new ItemStack[9];
        private ItemStack result;

        public IWShapedRecipe(ItemStack result) {
            this.result = result;
        }

        public IWShapedRecipe setShape(String shape) {
            if(shape.length() != 9)
                throw new IllegalArgumentException("Shape length must be 9!");
            this.shape = shape;
            return this;
        }

        public IWShapedRecipe set(char c, ItemStack item) {
            for (int i = 0; i <= 8; ++i)
                if (shape.charAt(i) == c)
                    stacks[i] = item.clone();
            return this;
        }

        public ItemStack[] getMatrix() {
            return stacks;
        }

        @Override
        public ItemStack getResult() {
            return result;
        }
    }

    public static class IWShapelessRecipe implements Recipe {
        private ItemStack[] stacks;
        private ItemStack result;
        private boolean useDurability = false;
        private int durabilityCost = 1;

	    public IWShapelessRecipe(ItemStack result, boolean justDurability) {
            this.result = result;
            this.useDurability = justDurability;
        }

        public IWShapelessRecipe set(ItemStack... item) {
            if(item.length > 9)
                throw new IllegalArgumentException("Array too long!");
            stacks = item;
            return this;
        }

        public ItemStack[] getMatrix() {
            return stacks;
        }

        @Override
        public ItemStack getResult() {
            return result;
        }

        public int getDurabilityCost() {
            return durabilityCost;
        }

        public IWShapelessRecipe setDurabilityCost(int durabilityCost) {
            this.durabilityCost = durabilityCost;
            return this;
        }

        public boolean isUseDurability() {
            return useDurability;
        }
    }
}
