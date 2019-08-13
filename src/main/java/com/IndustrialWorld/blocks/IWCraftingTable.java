package com.IndustrialWorld.blocks;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.event.TickEvent;
import com.IndustrialWorld.interfaces.BlockBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class IWCraftingTable extends BlockBase {
    private static LinkedHashMap<Inventory, Recipe> tickingMap = new LinkedHashMap<>();
    private static ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    public void onBreak(BlockBreakEvent event) {
        event.setDropItems(false);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), ConstItems.IW_CRAFTING_TABLE);
        super.onBreak(event);
    }

    @Override
    public void onInteractAsBlock(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            Inventory i = Bukkit.createInventory(null, InventoryType.WORKBENCH, "工业合成台");
            event.getPlayer().openInventory(i);
            tickingMap.put(i, null);
        }
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
            for (Recipe recipe : recipes) {
                Recipe recipeToSave = recipe;
                if(recipe instanceof IWShapedRecipe) {
                    for (int i = 0; i <= 8; ++i) {
                        ItemStack i1 = ci.getStorageContents()[i + 1]; // 必须要用getStorageContents/setStorageContents来对合成物品栏内的物品进行读/写！成品物品槽序号是0！
                        ItemStack i2 = ((IWShapedRecipe) recipe).getMatrix()[i];
                        if (i1 == null)
                            i1 = new ItemStack(Material.AIR);
                        if (i2 == null)
                            i2 = new ItemStack(Material.AIR);
                        if (i1.isSimilar(i2) && i1.getAmount() >= i2.getAmount())
                            ;
                        else
                            continue tag2;
                    }
                }else if(recipe instanceof IWShapelessRecipe) {
                    ArrayList<ItemStack> tempMatrix = new ArrayList<>();
                    ItemStack tempArray[] = new ItemStack[9];
                    Arrays.fill(tempArray, null);
                    tempMatrix.addAll(Arrays.asList(tempArray));
                    ArrayList<ItemStack> i1 = new ArrayList<>(Arrays.asList(ci.getStorageContents()).subList(1, ci.getStorageContents().length));
                    ItemStack[] i2 = ((IWShapelessRecipe) recipe).getMatrix();
                    t1:for(ItemStack ms : i2) {
                        for (ItemStack is : i1) {
                            if(is == null)
                                continue;
                            if(((IWShapelessRecipe) recipe).isUseDurability() && ms.getType().getMaxDurability() != 0) {
                                if(is.getType().getMaxDurability() != 0) {
                                    ItemStack bufI = is.clone();
                                    bufI.setDurability((short)0);
                                    if(bufI.equals(ms)) {
                                        tempMatrix.set(i1.indexOf(is), ms);
                                        i1.set(i1.indexOf(is), null);
                                        continue t1;
                                    }
                                }
                            }else if (ms.isSimilar(is) && is.getAmount() >= ms.getAmount()) {
                                tempMatrix.set(i1.indexOf(is), ms);
                                i1.set(i1.indexOf(is), null);
                                continue t1;
                            }
                        }
                        continue tag2;
                    }
                    recipeToSave = new IWShapelessRecipe(recipe.getResult(), ((IWShapelessRecipe) recipe).isUseDurability()).set(tempMatrix.toArray(new ItemStack[0])).setDurabilityCost(((IWShapelessRecipe) recipe).getDurabilityCost());
                }
                tickingMap.replace(ci, recipeToSave);
                ItemStack[] buf = ci.getStorageContents();
                buf[0] = recipe.getResult();
                ci.setStorageContents(buf);
                continue tag;
            }
            tickingMap.replace(ci, null);
            ItemStack[] buf = ci.getStorageContents();
            buf[0] = null;
            ci.setStorageContents(buf);
        }
    }

    public static void registerRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public static boolean isInvTicking(Inventory ci) {
        return tickingMap.containsKey(ci);
    }

    public static Recipe getRecipeUsing(Inventory ci) {
        return tickingMap.get(ci);
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

        public IWShapelessRecipe(ItemStack result) {
            this.result = result;
        }

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
