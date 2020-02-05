package com.IndustrialWorld.manager;

import com.IndustrialWorld.ConstItems;
import com.IndustrialWorld.blocks.BasicMachineBlock;
import com.IndustrialWorld.blocks.CopperOre;
import com.IndustrialWorld.blocks.IWCraftingTable;
import com.IndustrialWorld.i18n.I18n;
import com.IndustrialWorld.i18n.I18nConst;
import com.IndustrialWorld.manager.recipe.ShapedRecipeFactory;
import com.IndustrialWorld.manager.recipe.ShapelessRecipe;
import com.IndustrialWorld.utils.ItemStackUtil;
import com.IndustrialWorld.utils.MaterialUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RegisterManager {
    public static void registerIWCRecipes() {
        // Example:
        // RecipeRegistry.register(new ShapedRecipeFactory().map('A', new ItemStack(whatever)).pattern("3x3", "Any", "uwa").build(ItemStack you want));

        /* FORGE_HAMMER */{
        	ShapedRecipeFactory forgeHammerFactory = new ShapedRecipeFactory().map('A', new ItemStack(Material.IRON_INGOT)).map('B', new ItemStack(Material.STICK));
        	RecipeRegistry.register(forgeHammerFactory.pattern("AAC", "ABB", "AAC").build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [L]
            RecipeRegistry.register(forgeHammerFactory.pattern("CAA", "BBA", "CAA").build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [R]
		    RecipeRegistry.register(forgeHammerFactory.pattern("AAA", "ABA", "CBC").build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [U]
            RecipeRegistry.register(forgeHammerFactory.pattern("CBC", "ABA", "AAA").build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [D]

		    RecipeRegistry.register(new ShapelessRecipe(Arrays.asList(ConstItems.FORGE_HAMMER, new ItemStack(Material.IRON_INGOT)), ConstItems.IRON_PLATE).addItemCost(ConstItems.FORGE_HAMMER, 3));
            RecipeRegistry.register(new ShapelessRecipe(Arrays.asList(ConstItems.FORGE_HAMMER, ConstItems.COPPER_INGOT), ConstItems.COPPER_PLATE).addItemCost(ConstItems.FORGE_HAMMER, 3));
        }
        /* CUTTER */{
        	RecipeRegistry.register(new ShapedRecipeFactory().map('A', new ItemStack(Material.IRON_INGOT)).map('B', ConstItems.IRON_PLATE).pattern("BCB", "CBC", "ACA").build(ConstItems.CUTTER));
            RecipeRegistry.register(new ShapelessRecipe(Arrays.asList(ConstItems.COPPER_PLATE, ConstItems.FORGE_HAMMER), ConstItems.COPPER_WIRE).addItemCost(ConstItems.CUTTER, 4));
        }

        RecipeRegistry.register(new ShapedRecipeFactory().map('0', new ItemStack(Material.IRON_INGOT)).pattern("000", "0w0", "000").build(ConstItems.BASIC_MACHINE_BLOCK)); // BASIC_MACHINE_BLOCK

    }

    public static void registerBlockIS() {
        MainManager.register("BASIC_MACHINE_BLOCK", new BasicMachineBlock());
        MainManager.register("IW_CRAFTING_TABLE", new IWCraftingTable());

        MainManager.register("COPPER_ORE", new CopperOre());

        //getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "BASIC_MACHINE_BLOCK"), ConstItems.BASIC_MACHINE_BLOCK).shape("AAA", "ABA", "AAA").setIngredient('A', Material.IRON_INGOT).setIngredient('B', Material.AIR));
    }

    public static void registerItem() {
        // Forge Hammer
        ItemManager.register("FORGE_HAMMER", ItemStackUtil.create(MaterialUtil.IRON_SHOVEL).setId("FORGE_HAMMER").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER)).setLore(
                Arrays.asList(
                        ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER_LORE1),
                        ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER_LORE2),
                        ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER_LORE3)
                )).getItemStack());

        // IW Crafting Table
        ItemManager.register("IW_CRAFTING_TABLE", ItemStackUtil.create(Material.CRAFTING_TABLE).setId("IW_CRAFTING_TABLE").setAmount(1).setDisplayName(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE)).setLore(
                Arrays.asList(
                        ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE_LORE1),
                        ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE_LORE2)
                )).getItemStack());
    }
}
