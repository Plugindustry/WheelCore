package com.industrialworld.task;

import com.industrialworld.ConstItems;
import com.industrialworld.block.BasicMachineBlock;
import com.industrialworld.block.CopperOre;
import com.industrialworld.block.Grinder;
import com.industrialworld.i18n.I18n;
import com.industrialworld.i18n.I18nConst;
import com.industrialworld.item.Recognizer;
import com.industrialworld.manager.ItemMapping;
import com.industrialworld.manager.MainManager;
import com.industrialworld.manager.RecipeRegistry;
import com.industrialworld.manager.recipe.ShapedRecipeFactory;
import com.industrialworld.manager.recipe.ShapelessRecipe;
import com.industrialworld.manager.recipe.SmeltingRecipeImpl;
import com.industrialworld.utils.CompatMaterial;
import com.industrialworld.utils.ItemStackUtil;
import com.industrialworld.world.NormalOrePopulator;
import com.industrialworld.world.WorldGenCopperOre;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

// import com.industrialworld.block.IWCraftingTable;

public class RegisterTask {
    public static void registerRecipes() {
        // Example:
        // RecipeRegistry.register(new ShapedRecipeFactory().map('A', new ItemStack(whatever)).pattern("3x3", "Any",
        // "uwa").build(ItemStack you want));

        /* FORGE_HAMMER */
        {
            ShapedRecipeFactory forgeHammerFactory = new ShapedRecipeFactory().map('A',
                                                                                   new ItemStack(Material.IRON_INGOT))
                    .map('B', new ItemStack(Material.STICK));
            RecipeRegistry.register(forgeHammerFactory.pattern("AAC", "ABB", "AAC").build(ConstItems.FORGE_HAMMER),
                                    "forge_hammer_i_l"); // FORGE_HAMMER [L]
            RecipeRegistry.register(forgeHammerFactory.pattern("CAA", "BBA", "CAA").build(ConstItems.FORGE_HAMMER),
                                    "forge_hammer_i_r"); // FORGE_HAMMER [R]
            RecipeRegistry.register(forgeHammerFactory.pattern("AAA", "ABA", "CBC").build(ConstItems.FORGE_HAMMER),
                                    "forge_hammer_i_u"); // FORGE_HAMMER [U]
            RecipeRegistry.register(forgeHammerFactory.pattern("CBC", "ABA", "AAA").build(ConstItems.FORGE_HAMMER),
                                    "forge_hammer_i_d"); // FORGE_HAMMER [D]

            RecipeRegistry.register(new ShapelessRecipe(ConstItems.IRON_PLATE,
                                                        ConstItems.FORGE_HAMMER,
                                                        new ItemStack(Material.IRON_INGOT)).addItemCost(ConstItems.FORGE_HAMMER,
                                                                                                        3),
                                    "iron_plate");
            RecipeRegistry.register(new ShapelessRecipe(ConstItems.COPPER_PLATE,
                                                        ConstItems.FORGE_HAMMER,
                                                        ConstItems.COPPER_INGOT).addItemCost(ConstItems.FORGE_HAMMER,
                                                                                             3), "copper_plate");
            RecipeRegistry.register(new ShapelessRecipe(ConstItems.RED_HOT_STEEL_INGOT,
                                                        ConstItems.FORGE_HAMMER,
                                                        ConstItems.RED_HOT_IRON_INGOT).addItemCost(ConstItems.FORGE_HAMMER,
                                                                                                   3),
                                    "red_hot_steel_ingot");
        }
        /* CUTTER */
        {
            RecipeRegistry.register(new ShapedRecipeFactory().map('A', new ItemStack(Material.IRON_INGOT))
                                            .map('B',
                                                 ConstItems.IRON_PLATE)
                                            .pattern("BCB", "CBC", "ACA")
                                            .build(ConstItems.CUTTER), "cutter");
            RecipeRegistry.register(new ShapelessRecipe(ConstItems.COPPER_WIRE, ConstItems.COPPER_PLATE).addItemCost(
                    ConstItems.CUTTER,
                    4), "copper_wire");
        }

        // Basic Machine Block
        RecipeRegistry.register(new ShapedRecipeFactory().map('0', new ItemStack(Material.IRON_INGOT))
                                        .pattern("000", "0w0", "000")
                                        .build(ConstItems.BASIC_MACHINE_BLOCK),
                                "basic_machine_block"); // BASIC_MACHINE_BLOCK

        // Recognizer
        RecipeRegistry.register(new ShapedRecipeFactory().map('0', ConstItems.IRON_PLATE)
                                        .map('w',
                                             new ItemStack(Material.STICK))
                                        .pattern("000", "QwQ", "QwQ")
                                        .build(ItemMapping.get("RECOGNIZER")), "recognizer");

        // Smelting Recipes
        RecipeRegistry.register(new SmeltingRecipeImpl(new ItemStack(Material.IRON_INGOT),
                                                       ConstItems.RED_HOT_IRON_INGOT,
                                                       5.0F,
                                                       400), "red_hot_iron_ingot");
    }

    public static void registerBlock() {
        MainManager.register("BASIC_MACHINE_BLOCK", new BasicMachineBlock());
        // MainManager.register("IW_CRAFTING_TABLE", new IWCraftingTable());
        MainManager.register("GRINDER", new Grinder());

        MainManager.register("COPPER_ORE", new CopperOre());

        //getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "BASIC_MACHINE_BLOCK"), ConstItems
        // .BASIC_MACHINE_BK).shapLOCe("AAA", "ABA", "AAA").setIngredient('A', Material.IRON_INGOT).setIngredient
        // ('B', Material.AIR));
    }

    public static void registerItem() {
        // Forge Hammer
        ItemMapping.set("FORGE_HAMMER",
                        I18n.processItem("FORGE_HAMMER", ItemStackUtil.create(CompatMaterial.IRON_SHOVEL))
                                .getItemStack());

        /*// IW Crafting Table
        ItemManager.register("IW_CRAFTING_TABLE", ItemStackUtil.create(Material.CRAFTING_TABLE)
                .setId("IW_CRAFTING_TABLE")
                .setAmount(1)
                .setDisplayName(ChatColor.WHITE +
                                I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE))
                .setLore(Arrays.asList(ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE_LORE1),
                                       ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE_LORE2)))
                .getItemStack());*/
        ItemMapping.set("RECOGNIZER",
                        I18n.processItem("RECOGNIZER", ItemStackUtil.create(Material.STICK)).getItemStack());
        ItemMapping.set("IRON_PLATE",
                        I18n.processItem("IRON_PLATE",
                                         ItemStackUtil.create(Material.PAPER),
                                         Collections.emptyList(),
                                         Collections.singletonList(ChatColor.GRAY +
                                                                   I18n.getLocaleString(I18nConst.Element.Fe)))
                                .getItemStack());
        ItemMapping.set("CUTTER", I18n.processItem("CUTTER", ItemStackUtil.create(Material.SHEARS)).getItemStack());
        ItemMapping.set("BASIC_MACHINE_BLOCK",
                        I18n.processItem("BASIC_MACHINE_BLOCK", ItemStackUtil.create(Material.IRON_BLOCK))
                                .getItemStack());
        ItemMapping.set("COPPER_INGOT",
                        I18n.processItem("COPPER_INGOT", ItemStackUtil.create(Material.BRICK)).getItemStack());
        ItemMapping.set("COPPER_PLATE",
                        I18n.processItem("COPPER_PLATE", ItemStackUtil.create(Material.PAPER)).getItemStack());
        ItemMapping.set("COPPER_WIRE",
                        I18n.processItem("COPPER_WIRE", ItemStackUtil.create(Material.IRON_BARS)).getItemStack());
        ItemMapping.set("COPPER_ORE",
                        I18n.processItem("COPPER_ORE", ItemStackUtil.create(Material.IRON_ORE)).getItemStack());
        ItemMapping.set("RED_HOT_IRON_INGOT",
                        I18n.processItem("RED_HOT_IRON_INGOT", ItemStackUtil.create(Material.BRICK)).getItemStack());
        ItemMapping.set("STEEL_INGOT",
                        I18n.processItem("STEEL_INGOT", ItemStackUtil.create(Material.IRON_INGOT)).getItemStack());
        ItemMapping.set("RED_HOT_STEEL_INGOT",
                        I18n.processItem("RED_HOT_STEEL_INGOT", ItemStackUtil.create(Material.BRICK)).getItemStack());
        ItemMapping.set("QUICKLIME",
                        I18n.processItem("QUICKLIME", ItemStackUtil.create(Material.BONE_MEAL)).getItemStack());
        ItemMapping.set("TIN_INGOT",
                        I18n.processItem("TIN_INGOT", ItemStackUtil.create(Material.IRON_INGOT)).getItemStack());
        ItemMapping.set("ZINC_INGOT",
                        I18n.processItem("ZINC_INGOT", ItemStackUtil.create(Material.IRON_INGOT)).getItemStack());
        ItemMapping.set("GRINDER",
                        I18n.processItem("GRINDER", ItemStackUtil.create(Material.IRON_BLOCK)).getItemStack());
        ItemMapping.set("BASIC_MOTOR",
                        I18n.processItem("BASIC_MOTOR", ItemStackUtil.create(Material.SKELETON_SKULL)).getItemStack());
        ItemMapping.set("REDSTONE_BATTERY",
                        I18n.processItem("REDSTONE_BATTERY", ItemStackUtil.create(Material.SKELETON_SKULL))
                                .getItemStack());

        //ItemManager.register("INGOT_COPPER", ItemIngot.getInstance().getItemStack(IWMaterial.COPPER));

        MainManager.register("RECOGNIZER", new Recognizer());
    }

    public static void registerGenerator() {
        NormalOrePopulator.generators.add(new WorldGenCopperOre());
    }
}
