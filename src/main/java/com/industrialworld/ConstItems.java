package com.industrialworld;

import com.industrialworld.manager.ItemMapping;
import org.bukkit.inventory.ItemStack;

public final class ConstItems {
    public static ItemStack FORGE_HAMMER;
    public static ItemStack IW_CRAFTING_TABLE;
    public static ItemStack IRON_PLATE;
    public static ItemStack CUTTER;
    public static ItemStack BASIC_MACHINE_BLOCK;
    public static ItemStack COPPER_INGOT;
    public static ItemStack COPPER_PLATE;
    public static ItemStack COPPER_WIRE;
    public static ItemStack COPPER_ORE;
    public static ItemStack RED_HOT_IRON_INGOT;
    public static ItemStack STEEL_INGOT;
    public static ItemStack RED_HOT_STEEL_INGOT;
    public static ItemStack QUICKLIME;
    public static ItemStack TIN_INGOT;
    public static ItemStack ZINC_INGOT;
    public static ItemStack LEAD_INGOT;
    public static ItemStack GRINDER;
    public static ItemStack BASIC_MOTOR;
    public static ItemStack REDSTONE_BATTERY;
    public static ItemStack BASIC_COIL;

    public static void initConst() {
        FORGE_HAMMER = ItemMapping.get("FORGE_HAMMER");
        IW_CRAFTING_TABLE = ItemMapping.get("IW_CRAFTING_TABLE");
        IRON_PLATE = ItemMapping.get("IRON_PLATE");
        CUTTER = ItemMapping.get("CUTTER");
        BASIC_MACHINE_BLOCK = ItemMapping.get("BASIC_MACHINE_BLOCK");
        COPPER_INGOT = ItemMapping.get("COPPER_INGOT");
        COPPER_PLATE = ItemMapping.get("COPPER_PLATE");
        COPPER_WIRE = ItemMapping.get("COPPER_WIRE");
        COPPER_ORE = ItemMapping.get("COPPER_ORE");
        RED_HOT_IRON_INGOT = ItemMapping.get("RED_HOT_IRON_INGOT");
        STEEL_INGOT = ItemMapping.get("STEEL_INGOT");
        RED_HOT_STEEL_INGOT = ItemMapping.get("RED_HOT_STEEL_INGOT");
        QUICKLIME = ItemMapping.get("QUICKLIME");
        TIN_INGOT = ItemMapping.get("TIN_INGOT");
        ZINC_INGOT = ItemMapping.get("ZINC_INGOT");
        LEAD_INGOT = ItemMapping.get("LEAD_INGOT");
        GRINDER = ItemMapping.get("GRINDER");
        BASIC_MOTOR = ItemMapping.get("BASIC_MOTOR");
        REDSTONE_BATTERY = ItemMapping.get("REDSTONE_BATTERY");
        BASIC_COIL = ItemMapping.get("BASIC_COIL");
    }
}
