package com.IndustrialWorld;

import com.IndustrialWorld.i18n.I18n;
import com.IndustrialWorld.i18n.I18nConst;
import com.IndustrialWorld.utils.NBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class ConstItems {

    public final static ItemStack FORGE_HAMMER = genIWIS(Material.IRON_SHOVEL, "FORGE_HAMMER", ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER_LORE2),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.FORGE_HAMMER_LORE3)
            ));
    public final static ItemStack IW_CRAFTING_TABLE = genIWIS(Material.CRAFTING_TABLE, "IW_CRAFTING_TABLE", ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.IW_CRAFTING_TABLE_LORE2)
            ));
    public final static ItemStack IRON_PLATE = genIWIS(Material.PAPER, "IRON_PLATE", ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IRON_PLATE),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.IRON_PLATE_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.IRON_PLATE_LORE2),
                    ChatColor.GREEN + I18n.getLocaleString(I18nConst.Element.Fe)
            ));
    public final static ItemStack CUTTER = genIWIS(Material.SHEARS, "CUTTER", ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.CUTTER),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.CUTTER),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.CUTTER_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.CUTTER_LORE2)
            ));
    public final static ItemStack BASIC_MACHINE_BLOCK = genIWIS(Material.IRON_BLOCK, "BASIC_MACHINE_BLOCK", ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.BASIC_MACHINE_BLOCK),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.BASIC_MACHINE_BLOCK_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.BASIC_MACHINE_BLOCK_LORE2)
            ));
    public final static ItemStack COPPER_INGOT = genIWIS(Material.BRICK, "COPPER_INGOT", ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_INGOT),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_INGOT_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_INGOT_LORE2),
                    ChatColor.GREEN + I18n.getLocaleString(I18nConst.Element.Cu)
            ));
    public final static ItemStack COPPER_PLATE = genIWIS(Material.PAPER, "COPPER_PLATE", ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_PLATE),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_PLATE_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_PLATE_LORE2),
                    ChatColor.GREEN + I18n.getLocaleString(I18nConst.Element.Cu)
            ));
    public final static ItemStack COPPER_WIRE = genIWIS(Material.IRON_BARS, "COPPER_WIRE", ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_WIRE),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_WIRE_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_WIRE_LORE2),
                    ChatColor.GREEN + I18n.getLocaleString(I18nConst.Element.Cu)
            ));
    public final static ItemStack COPPER_ORE = genIWIS(Material.IRON_ORE, "COPPER_ORE", ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_ORE),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_ORE_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_ORE_LORE2)
            ));

    /*
    public final static ItemStack IW_FURNACE = genIWIS(Material.FURNACE, "IW_FURNACE", ChatColor.WHITE + "工业熔炼炉",
            Arrays.asList(
                ChatColor.WHITE + "工业用熔炼炉",
                ChatColor.GRAY + "工业矿石加工时均需使用"
             ));
    public final static ItemStack IW_TRASH_COAL = genIWIS(Material.CHARCOAL, "IW_TRASH_COAL", ChatColor.WHITE + "粗制燃料",
            Arrays.asList(
                ChatColor.WHITE + "粗制的燃料",
                ChatColor.GRAY + "能提供少量热量"
             ));
    public final static ItemStack FIRE_GENERATOR = genIWIS(Material.FURNACE, "FIRE_GENERATOR", ChatColor.WHITE + "火力发电机",
            Arrays.asList(
                    ChatColor.WHITE + "由热驱动的发电机",
                    ChatColor.GRAY + "可消耗燃料供电",
                    ChatColor.GRAY + "机械效率约为30%"
            ));
    public final static ItemStack LIGHT_GENERATOR = genIWIS(Material.DAYLIGHT_DETECTOR, "LIGHT_GENERATOR", ChatColor.WHITE + "太阳能电池板",
            Arrays.asList(
                    ChatColor.WHITE + "由太阳能驱动的发电机",
                    ChatColor.GREEN + "纯天然绿色无污染,节能环保",
                    ChatColor.GRAY + "可在阳光下缓慢发电"
            ));
    public final static ItemStack WATER_GENERATOR = genIWIS(Material.FURNACE, "WATER_GENERATOR", ChatColor.WHITE + "水力发电机",
            Arrays.asList(
                ChatColor.WHITE + "由水力驱动的发电机",
                ChatColor.GREEN + "据说三峡用的就是这款牌子的水力发电机",
                ChatColor.GRAY + "可在阳光下缓慢发电"
             ));*/

    // IW ItemStack generator

    private static ItemStack genIWIS(Material mtrl, String itemId, String displayName, List<String> lore) {
        ItemStack tmp = new ItemStack(mtrl, 1);
        tmp = NBTUtil.setTagValue(tmp, "isIWItem", new NBTUtil.NBTValue().set(true));
        tmp = NBTUtil.setTagValue(tmp, "IWItemId", new NBTUtil.NBTValue().set(itemId));
        ItemMeta meta = tmp.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        tmp.setItemMeta(meta);
        return tmp.clone();
    }

}
