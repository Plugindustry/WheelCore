package com.industrialworld.manager;

import com.industrialworld.ConstItems;
import com.industrialworld.block.BasicMachineBlock;
import com.industrialworld.block.CopperOre;
import com.industrialworld.block.Grinder;
import com.industrialworld.i18n.I18n;
import com.industrialworld.i18n.I18nConst;
import com.industrialworld.item.ItemType;
import com.industrialworld.item.Recognizer;
import com.industrialworld.item.material.IWMaterial;
import com.industrialworld.item.material.info.MaterialInfo;
import com.industrialworld.item.template.ItemIngot;
import com.industrialworld.item.template.ItemTemplate;
import com.industrialworld.manager.recipe.ShapedRecipeFactory;
import com.industrialworld.manager.recipe.ShapelessRecipe;
import com.industrialworld.manager.recipe.SmeltingRecipeImpl;
import com.industrialworld.utils.ItemStackUtil;
import com.industrialworld.utils.MaterialUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

// import com.industrialworld.block.IWCraftingTable;

public class RegisterManager {
    public static void registerIWCRecipes() {
        // Example:
        // RecipeRegistry.register(new ShapedRecipeFactory().map('A', new ItemStack(whatever)).pattern("3x3", "Any",
        // "uwa").build(ItemStack you want));

        /* FORGE_HAMMER */
        {
            ShapedRecipeFactory forgeHammerFactory = new ShapedRecipeFactory().map('A', ItemType.INGOT).map('B',
                                                                                                            new ItemStack(
                                                                                                                    Material.STICK));
            RecipeRegistry.register(forgeHammerFactory.pattern("AAC", "ABB", "AAC")
                                            .build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [L]
            RecipeRegistry.register(forgeHammerFactory.pattern("CAA", "BBA", "CAA")
                                            .build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [R]
            RecipeRegistry.register(forgeHammerFactory.pattern("AAA", "ABA", "CBC")
                                            .build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [U]
            RecipeRegistry.register(forgeHammerFactory.pattern("CBC", "ABA", "AAA")
                                            .build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [D]
            forgeHammerFactory.map('A', new ItemStack(Material.IRON_INGOT));
            RecipeRegistry.register(forgeHammerFactory.pattern("AAC", "ABB", "AAC")
                                            .build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [L]
            RecipeRegistry.register(forgeHammerFactory.pattern("CAA", "BBA", "CAA")
                                            .build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [R]
            RecipeRegistry.register(forgeHammerFactory.pattern("AAA", "ABA", "CBC")
                                            .build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [U]
            RecipeRegistry.register(forgeHammerFactory.pattern("CBC", "ABA", "AAA")
                                            .build(ConstItems.FORGE_HAMMER)); // FORGE_HAMMER [D]

            RecipeRegistry.register(new ShapelessRecipe(Arrays.asList(ConstItems.FORGE_HAMMER,
                                                                      new ItemStack(Material.IRON_INGOT)),
                                                        ConstItems.IRON_PLATE).addItemCost(ConstItems.FORGE_HAMMER, 3));
            RecipeRegistry.register(new ShapelessRecipe(Arrays.asList(ConstItems.FORGE_HAMMER, ConstItems.COPPER_INGOT),
                                                        ConstItems.COPPER_PLATE).addItemCost(ConstItems.FORGE_HAMMER,
                                                                                             3));
            RecipeRegistry.register(new ShapelessRecipe(Arrays.asList(ConstItems.FORGE_HAMMER,
                                                                      ConstItems.RED_HOT_IRON_INGOT),
                                                        ConstItems.RED_HOT_STEEL_INGOT).addItemCost(ConstItems.FORGE_HAMMER,
                                                                                                    3));
        }
        /* CUTTER */
        {
            RecipeRegistry.register(new ShapedRecipeFactory().map('A', new ItemStack(Material.IRON_INGOT))
                                            .map('B',
                                                 ConstItems.IRON_PLATE)
                                            .pattern("BCB", "CBC", "ACA")
                                            .build(ConstItems.CUTTER));
            RecipeRegistry.register(new ShapelessRecipe(Collections.singletonList(ConstItems.COPPER_PLATE),
                                                        ConstItems.COPPER_WIRE).addItemCost(ConstItems.CUTTER, 4));
        }

        // Basic Machine Block
        RecipeRegistry.register(new ShapedRecipeFactory().map('0', new ItemStack(Material.IRON_INGOT))
                                        .pattern("000", "0w0", "000")
                                        .build(ConstItems.BASIC_MACHINE_BLOCK)); // BASIC_MACHINE_BLOCK

        // Recognizer
        RecipeRegistry.register(new ShapedRecipeFactory().map('0', ConstItems.IRON_PLATE)
                                        .map('w',
                                             new ItemStack(Material.STICK))
                                        .pattern("000", "QwQ", "QwQ")
                                        .build(ItemManager.get("RECOGNIZER")));

        // Smelting Recipes
        RecipeRegistry.register(new SmeltingRecipeImpl(new ItemStack(Material.IRON_INGOT),
                                                       ConstItems.RED_HOT_IRON_INGOT,
                                                       5.0F,
                                                       400));
    }

    public static void registerBlockIS() {
        MainManager.register("BASIC_MACHINE_BLOCK", new BasicMachineBlock());
        // MainManager.register("IW_CRAFTING_TABLE", new IWCraftingTable());
        MainManager.register("GRINDER", new Grinder());

        MainManager.register("COPPER_ORE", new CopperOre());

        //getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "BASIC_MACHINE_BLOCK"), ConstItems
        // .BASIC_MACHINE_BK).shapLOCe("AAA", "ABA", "AAA").setIngredient('A', Material.IRON_INGOT).setIngredient
        // ('B', Material.AIR));
    }

    public static void registerMaterial() {
        ItemTemplate itemIngot = ItemIngot.getInstance();
        itemIngot.register(IWMaterial.COPPER);
        itemIngot.register(IWMaterial.STEEL);
    }

    public static void registerItem() {
        // Forge Hammer
        ItemManager.register("FORGE_HAMMER",
                             I18n.processItem("FORGE_HAMMER", 3, ItemStackUtil.create(MaterialUtil.IRON_SHOVEL))
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
        ItemManager.register("RECOGNIZER",
                             I18n.processItem("RECOGNIZER", 2, ItemStackUtil.create(Material.STICK)).getItemStack());
        ItemManager.register("IRON_PLATE",
                             I18n.processItem("IRON_PLATE",
                                              1,
                                              ItemStackUtil.create(Material.PAPER),
                                              Collections.emptyList(),
                                              Collections.singletonList(ChatColor.GRAY +
                                                                        I18n.getLocaleString(I18nConst.Element.Fe)))
                                     .getItemStack());
        ItemManager.register("CUTTER",
                             I18n.processItem("CUTTER", 3, ItemStackUtil.create(Material.SHEARS)).getItemStack());
        ItemManager.register("BASIC_MACHINE_BLOCK",
                             I18n.processItem("BASIC_MACHINE_BLOCK", 2, ItemStackUtil.create(Material.IRON_BLOCK))
                                     .getItemStack());
        ItemManager.register("COPPER_INGOT", ItemIngot.getInstance().getItemStack(IWMaterial.COPPER));
        ItemManager.register("COPPER_PLATE",
                             I18n.processItem("COPPER_PLATE", 2, ItemStackUtil.create(Material.PAPER)).getItemStack());
        ItemManager.register("COPPER_WIRE",
                             I18n.processItem("COPPER_WIRE", 2, ItemStackUtil.create(Material.IRON_BARS))
                                     .getItemStack());
        ItemManager.register("COPPER_ORE",
                             I18n.processItem("COPPER_ORE", 2, ItemStackUtil.create(Material.IRON_ORE)).getItemStack());
        ItemManager.register("RED_HOT_IRON_INGOT",
                             I18n.processItem("RED_HOT_IRON_INGOT", 2, ItemStackUtil.create(Material.BRICK))
                                     .getItemStack());
        ItemManager.register("STEEL_INGOT", ItemIngot.getInstance().getItemStack(IWMaterial.STEEL));
        ItemManager.register("RED_HOT_STEEL_INGOT",
                             I18n.processItem("RED_HOT_STEEL_INGOT", 2, ItemStackUtil.create(Material.BRICK))
                                     .getItemStack());
        ItemManager.register("QUICKLIME",
                             I18n.processItem("QUICKLIME", 2, ItemStackUtil.create(Material.BONE_MEAL)).getItemStack());
        ItemManager.register("TIN_INGOT",
                             I18n.processItem("TIN_INGOT", 2, ItemStackUtil.create(Material.IRON_INGOT))
                                     .getItemStack());
        ItemManager.register("ZINC_INGOT",
                             I18n.processItem("ZINC_INGOT", 2, ItemStackUtil.create(Material.IRON_INGOT))
                                     .getItemStack());
        ItemManager.register("GRINDER",
                             I18n.processItem("GRINDER", 2, ItemStackUtil.create(Material.IRON_BLOCK)).getItemStack());
        ItemManager.register("BASIC_MOTOR",
                             I18n.processItem("BASIC_MOTOR", 2, ItemStackUtil.create(Material.SKELETON_SKULL))
                                     .getItemStack());
        ItemManager.register("REDSTONE_BATTERY",
                             I18n.processItem("REDSTONE_BATTERY", 2, ItemStackUtil.create(Material.SKELETON_SKULL))
                                     .getItemStack());

        //ItemManager.register("INGOT_COPPER", ItemIngot.getInstance().getItemStack(IWMaterial.COPPER));

        MainManager.register("RECOGNIZER", new Recognizer());
    }

    public static void registerIWMaterial() {
        IWMaterialManager.register(IWMaterial.COPPER, MaterialInfo.newInstance(IWMaterial.COPPER).setLevel((short) 3));
    }

    public static void registerIWItemMaterial() {
        ItemManager.registerItemMaterial(ItemType.INGOT,
                                         IWMaterial.NULL,
                                         Material.IRON_INGOT); // NULL Ingot -> Iron Ingot (For placeholder)
        ItemManager.registerItemMaterial(ItemType.INGOT, IWMaterial.COPPER, Material.BRICK); // Copper Ingot -> Brick
        ItemManager.registerItemMaterial(ItemType.INGOT,
                                         IWMaterial.STEEL,
                                         Material.IRON_INGOT); // Steel Ingot -> Iron Ingot
    }
}
