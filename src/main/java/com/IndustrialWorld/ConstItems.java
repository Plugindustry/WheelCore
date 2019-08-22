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
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.IRON_PLATE_LORE2)
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
    public final static ItemStack COPPER_INGOT = genIWIS(Material.BRICK, "COPPER_INGOT", ChatColor.WHITE + I18n.getLocaleString(I18nConst.COPPER_INGOT),
            Arrays.asList(
                    ChatColor.WHITE + I18n.getLocaleString(I18nConst.Item.COPPER_INGOT_LORE1),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_INGOT_LORE2),
                    ChatColor.GRAY + I18n.getLocaleString(I18nConst.Item.COPPER_INGOT_EL)
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
        meta.setDisplayName(ChatColor.WHITE + displayName);
        meta.setLore(lore);
        tmp.setItemMeta(meta);
        return tmp.clone();
    }

}


//
//    static {
//        ItemStack tmp1 = new ItemStack(Material.IRON_BLOCK, 1);
//        tmp1 = NBTUtil.setTagValue(tmp1, "isIWItem", new NBTUtil.NBTValue().set(true));
//        tmp1 = NBTUtil.setTagValue(tmp1, "IWItemId", new NBTUtil.NBTValue().set("BASIC_MACHINE_BLOCK"));
//        ItemMeta meta1 = tmp1.getItemMeta();
//        meta1.setDisplayName(ChatColor.WHITE + "基础机器方块");
//        meta1.setLore(Arrays.asList(
//                ChatColor.WHITE + "最基本的机器外壳",
//                ChatColor.GRAY + "可用于制造基础机器"
//        ));
//        tmp1.setItemMeta(meta1);
//        BASIC_MACHINE_BLOCK = tmp1.clone();
//
//        ItemStack tmp2 = new ItemStack(Material.CRAFTING_TABLE, 1);
//        tmp2 = NBTUtil.setTagValue(tmp2, "isIWItem", new NBTUtil.NBTValue().set(true));
//        tmp2 = NBTUtil.setTagValue(tmp2, "IWItemId", new NBTUtil.NBTValue().set("IW_CRAFTING_TABLE"));
//        ItemMeta meta2 = tmp2.getItemMeta();
//        meta2.setDisplayName(ChatColor.WHITE + "工业合成台");
//        meta2.setLore(Arrays.asList(
//                ChatColor.WHITE + "工业用合成台",
//                ChatColor.GRAY + "工业物品合成时均需使用"
//                                  ));
//        tmp2.setItemMeta(meta2);
//        IW_CRAFTING_TABLE = tmp2.clone();
//
//        ItemStack tmp3 = new ItemStack(Material.IRON_BARS, 1);
//        tmp3 = NBTUtil.setTagValue(tmp2, "isIWItem", new NBTUtil.NBTValue().set(true));
//        tmp3 = NBTUtil.setTagValue(tmp2, "IWItemId", new NBTUtil.NBTValue().set("IW_WIRE"));
//        ItemMeta meta3 = tmp3.getItemMeta();
//        meta3.setDisplayName(ChatColor.WHITE + "导线");
//        meta3.setLore(Arrays.asList(
//                ChatColor.WHITE + "工业线缆",
//                ChatColor.GRAY + "用来在各机器之间传输电力"
//        ));
//        tmp2.setItemMeta(meta3);
//        IW_WIRE = tmp2.clone();
//    }
//}
