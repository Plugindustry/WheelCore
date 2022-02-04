package io.github.plugindustry.wheelcore.task;

import io.github.plugindustry.wheelcore.manager.ItemMapping;
import org.bukkit.Material;

import java.util.EnumSet;
import java.util.HashSet;

public class RegisterTask {
    public static void registerRecipes() {
        // Example:
        // RecipeRegistry.register(new ShapedRecipeFactory().map('A', new ItemStack(whatever)).pattern("3x3", "Any",
        // "uwa").build(ItemStack you want));
    }

    public static void registerBlock() {

    }

    public static void registerItem() {

    }

    public static void registerGenerator() {
        //NormalOrePopulator.generators.add(new WorldGenCopperOre());
    }

    public static void registerVanillaOreDict() {
        EnumSet<Material> food = EnumSet.of(Material.APPLE,
                                            Material.BREAD,
                                            Material.COOKED_BEEF,
                                            Material.COOKED_CHICKEN,
                                            Material.COOKED_COD,
                                            Material.COOKED_MUTTON,
                                            Material.COOKED_PORKCHOP,
                                            Material.COOKED_RABBIT,
                                            Material.COOKED_SALMON,
                                            Material.COOKIE,
                                            Material.GOLDEN_APPLE,
                                            Material.ENCHANTED_GOLDEN_APPLE,
                                            Material.GOLDEN_CARROT,
                                            Material.MUSHROOM_STEW,
                                            Material.MELON_SLICE,
                                            Material.POTATO,
                                            Material.PUMPKIN_PIE,
                                            Material.RABBIT_STEW,
                                            Material.BEEF,
                                            Material.CHICKEN,
                                            Material.COD,
                                            Material.MUTTON,
                                            Material.PORKCHOP,
                                            Material.RABBIT,
                                            Material.SALMON,
                                            Material.SPIDER_EYE,
                                            Material.ROTTEN_FLESH);
        for (Material type : Material.values()) {
            if (!type.isItem())
                continue;
            HashSet<String> dict = new HashSet<>();
            if (type.isRecord())
                dict.add("record");
            if (type.hasGravity())
                dict.add("gravity");
            if (type.isFuel())
                dict.add("fuel");
            if (type.getMaxDurability() > 0)
                dict.add("tool");
            if (type.isBlock())
                dict.add("block");

            String name = type.name();
            if (name.endsWith("_HELMET"))
                dict.add("helmet");
            if (name.endsWith("_CHESTPLATE"))
                dict.add("chestplate");
            if (name.endsWith("_LEGGINGS"))
                dict.add("leggings");
            if (name.endsWith("_BOOTS"))
                dict.add("boots");
            if (name.endsWith("_SWORD"))
                dict.add("sword");
            if (name.endsWith("_AXE"))
                dict.add("axe");
            if (name.endsWith("_PICKAXE"))
                dict.add("pickaxe");
            if (name.endsWith("_SHOVEL"))
                dict.add("shovel");
            if (name.endsWith("_HOE"))
                dict.add("hoe");
            if (name.endsWith("_HORSE_ARMOR"))
                dict.add("horse_armor");
            if (name.equals("BOW"))
                dict.add("bow");
            if (name.equals("FISHING_ROD"))
                dict.add("fishing_rod");
            if (name.equals("SHIELD"))
                dict.add("shield");
            if (name.equals("ELYTRA"))
                dict.add("elytra");
            if (name.equals("TRIDENT"))
                dict.add("trident");
            if (name.equals("SHULKER_SHELL"))
                dict.add("shulker_shell");
            if (name.equals("TOTEM_OF_UNDYING"))
                dict.add("totem_of_undying");
            if (name.endsWith("_SHULKER_BOX"))
                dict.add("shulker_box");
            if (name.endsWith("_BANNER"))
                dict.add("banner");
            if (name.endsWith("_BED"))
                dict.add("bed");
            if (name.endsWith("_PLANKS"))
                dict.add("planks");
            if (name.endsWith("_LOG"))
                dict.add("log");

            if (food.contains(type))
                dict.add("food");

            ItemMapping.registerVanillaOreDict(type, dict);
        }
    }
}
