package com.IndustrialWorld;

import com.IndustrialWorld.utils.NBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class ConstItems {

    public final static ItemStack BASIC_MACHINE_BLOCK = genIWIS(Material.IRON_BLOCK, "BASIC_MACHINE_BLOCK", ChatColor.WHITE + "基础机器方块",
            Arrays.asList(
                ChatColor.WHITE + "最基本的机器外壳",
                ChatColor.GRAY + "可用于制造基础机器"
             ));
    public final static ItemStack IW_CRAFTING_TABLE = genIWIS(Material.CRAFTING_TABLE, "IW_CRAFTING_TABLE", ChatColor.WHITE + "工业合成台",
            Arrays.asList(
                ChatColor.WHITE + "工业用合成台",
                ChatColor.GRAY + "工业物品合成时均需使用"
             ));
    public final static ItemStack IW_WIRE = genIWIS(Material.IRON_BARS, "IW_WIRE", ChatColor.WHITE + "线缆",
            Arrays.asList(
                    ChatColor.WHITE + "工业用线缆",
                    ChatColor.GRAY + "用于各机器间传输电力"
            ));
    public final static ItemStack FORGE_HAMMER = genIWIS(Material.IRON_SHOVEL, "FORGE_HAMMER", ChatColor.WHITE + "锻造锤",
            Arrays.asList(
                    ChatColor.WHITE + "在工作台上砸砸砸砸",
                    ChatColor.GRAY + "3耐久/次",
                    ChatColor.GRAY + "(当然，你也可以把它当铲子用，不过似乎太浪费了)"
            ));
    public final static ItemStack IRON_PLATE = genIWIS(Material.PAPER, "IRON_PLATE", ChatColor.WHITE + "铁板",
            Arrays.asList(
                    ChatColor.WHITE + "铁板",
                    ChatColor.GRAY + "*看起来很轻(?)"
            ));
    public final static ItemStack CUTTER = genIWIS(Material.SHEARS, "CUTTER", ChatColor.WHITE + "板材切割剪刀",
            Arrays.asList(
                    ChatColor.WHITE + "板材切割剪刀",
                    ChatColor.GRAY + "把板材切割成线"
            ));


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
