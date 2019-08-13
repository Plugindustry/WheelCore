package com.IndustrialWorld.manager;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.blocks.BasicMachineBlock;
import com.IndustrialWorld.blocks.IWCraftingTable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RegisterManager {
    public static void registerIWCRecipes() {
        // Example:
        //IWCraftingTable.registerRecipe(new IWCraftingTable.IWShapedRecipe(ConstItems.BASIC_MACHINE_BLOCK).setShape("AAAAAAAAA").set('A', new ItemStack(Material.IRON_INGOT, 2)));

        /* FORGE_HAMMER */
        {
            IWCraftingTable.registerRecipe(new IWCraftingTable.IWShapedRecipe(ConstItems.FORGE_HAMMER).setShape("AACABBAAC").set('A', new ItemStack(Material.IRON_INGOT, 1)).set('B', new ItemStack(Material.STICK, 1)).set('C', new ItemStack(Material.AIR, 1))); // FORGE_HAMMER [L]
            IWCraftingTable.registerRecipe(new IWCraftingTable.IWShapedRecipe(ConstItems.FORGE_HAMMER).setShape("CAABBACAA").set('A', new ItemStack(Material.IRON_INGOT, 1)).set('B', new ItemStack(Material.STICK, 1)).set('C', new ItemStack(Material.AIR, 1))); // FORGE_HAMMER [R]
            IWCraftingTable.registerRecipe(new IWCraftingTable.IWShapedRecipe(ConstItems.FORGE_HAMMER).setShape("AAAABACBC").set('A', new ItemStack(Material.IRON_INGOT, 1)).set('B', new ItemStack(Material.STICK, 1)).set('C', new ItemStack(Material.AIR, 1))); // FORGE_HAMMER [U]
            IWCraftingTable.registerRecipe(new IWCraftingTable.IWShapedRecipe(ConstItems.FORGE_HAMMER).setShape("CBCABAAAA").set('A', new ItemStack(Material.IRON_INGOT, 1)).set('B', new ItemStack(Material.STICK, 1)).set('C', new ItemStack(Material.AIR, 1))); // FORGE_HAMMER [D]
            IWCraftingTable.registerRecipe(new IWCraftingTable.IWShapelessRecipe(ConstItems.IRON_PLATE, true).set(ConstItems.FORGE_HAMMER, new ItemStack(Material.IRON_INGOT)).setDurabilityCost(3));
        }

        IWCraftingTable.registerRecipe(new IWCraftingTable.IWShapedRecipe(ConstItems.CUTTER).setShape("BCBCBCACA").set('A', new ItemStack(Material.IRON_INGOT, 1)).set('B', ConstItems.IRON_PLATE).set('C', new ItemStack(Material.AIR, 1))); // CUTTER
    }

    public static void registerBlockIS() {
        MainManager.register("BASIC_MACHINE_BLOCK", new BasicMachineBlock());
        MainManager.register("IW_CRAFTING_TABLE", new IWCraftingTable());

        //getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "BASIC_MACHINE_BLOCK"), ConstItems.BASIC_MACHINE_BLOCK).shape("AAA", "ABA", "AAA").setIngredient('A', Material.IRON_INGOT).setIngredient('B', Material.AIR));
    }
}
