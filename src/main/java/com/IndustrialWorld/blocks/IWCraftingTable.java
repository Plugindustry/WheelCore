package com.IndustrialWorld.blocks;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.event.TickEvent;
import com.IndustrialWorld.interfaces.BlockBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class IWCraftingTable extends BlockBase {
    private static LinkedHashMap<Inventory, IWRecipe> tickingMap = new LinkedHashMap<>();
    private static ArrayList<IWRecipe> recipes = new ArrayList<>();

    @Override
    public void onBreak(BlockBreakEvent event) {
        event.setDropItems(false);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ConstItems.IW_CRAFTING_TABLE);
        super.onBreak(event);
    }

    @Override
    public void onInteractAsBlock(PlayerInteractEvent event) {
        event.setCancelled(true);
        Inventory i = Bukkit.createInventory(null, InventoryType.WORKBENCH, "工业合成台");
        event.getPlayer().openInventory(i);
        tickingMap.put(i, null);
    }

    @Override
    public void onTick(TickEvent event) {
        tag:
        for (Inventory ci : tickingMap.keySet()) {
            if (ci.getViewers().isEmpty()) {
                tickingMap.remove(ci);
                continue;
            }
            tag2:
            for (IWRecipe recipe : recipes) {
                for (int i = 0; i <= 8; ++i) {
                    ItemStack i1 = ci.getStorageContents()[i + 1]; // 必须要用getStorageContents/setStorageContents来对合成物品栏内的物品进行读/写！成品物品槽序号是0！
                    ItemStack i2 = recipe.getMatrix()[i];
                    if (i1 == null)
                        i1 = new ItemStack(Material.AIR);
                    if (i2 == null)
                        i2 = new ItemStack(Material.AIR);
                    if (i1.isSimilar(i2) && i1.getAmount() >= i2.getAmount())
                        ;
                    else
                        continue tag2;
                }
                tickingMap.replace(ci, recipe);
                ItemStack[] buf = ci.getStorageContents();
                buf[0] = recipe.getResult();
                ci.setStorageContents(buf);
                continue tag;
            }
            tickingMap.replace(ci, null);
        }
    }

    public static void registerRecipe(IWRecipe recipe) {
        recipes.add(recipe);
    }

    public static boolean isInvTicking(Inventory ci) {
        return tickingMap.containsKey(ci);
    }

    public static IWRecipe getRecipeUsing(Inventory ci) {
        return tickingMap.get(ci);
    }

    public static class IWRecipe implements Recipe {
        private String shape;
        private ItemStack[] stacks = new ItemStack[9];
        private ItemStack result;

        public IWRecipe(ItemStack result) {
            this.result = result;
        }

        public IWRecipe setShape(String shape) {
            if(shape.length() != 9)
                throw new IllegalArgumentException("Shape length must be 9!");
            this.shape = shape;
            return this;
        }

        public IWRecipe set(char c, ItemStack item) {
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
}
