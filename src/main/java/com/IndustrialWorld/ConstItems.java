package com.IndustrialWorld;

import com.IndustrialWorld.utils.NBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class ConstItems {
    public final static ItemStack BASIC_MACHINE_BLOCK;
    public final static ItemStack IW_CRAFTING_TABLE;
    public final static ItemStack IW_WIRE;

    static {
        ItemStack tmp1 = new ItemStack(Material.IRON_BLOCK, 1);
        tmp1 = NBTUtil.setTagValue(tmp1, "isIWItem", new NBTUtil.NBTValue().set(true));
        tmp1 = NBTUtil.setTagValue(tmp1, "IWItemId", new NBTUtil.NBTValue().set("BASIC_MACHINE_BLOCK"));
        ItemMeta meta1 = tmp1.getItemMeta();
        meta1.setDisplayName(ChatColor.WHITE + "基础机器方块");
        meta1.setLore(Arrays.asList(
                ChatColor.WHITE + "最基本的机器外壳",
                ChatColor.GRAY + "可用于制造基础机器"
        ));
        tmp1.setItemMeta(meta1);
        BASIC_MACHINE_BLOCK = tmp1.clone();

        ItemStack tmp2 = new ItemStack(Material.CRAFTING_TABLE, 1);
        tmp2 = NBTUtil.setTagValue(tmp2, "isIWItem", new NBTUtil.NBTValue().set(true));
        tmp2 = NBTUtil.setTagValue(tmp2, "IWItemId", new NBTUtil.NBTValue().set("IW_CRAFTING_TABLE"));
        ItemMeta meta2 = tmp2.getItemMeta();
        meta2.setDisplayName(ChatColor.WHITE + "工业合成台");
        meta2.setLore(Arrays.asList(
                ChatColor.WHITE + "工业用合成台",
                ChatColor.GRAY + "工业物品合成时均需使用"
                                  ));
        tmp2.setItemMeta(meta2);
        IW_CRAFTING_TABLE = tmp2.clone();

        ItemStack tmp3 = new ItemStack(Material.IRON_BARS, 1);
        tmp3 = NBTUtil.setTagValue(tmp2, "isIWItem", new NBTUtil.NBTValue().set(true));
        tmp3 = NBTUtil.setTagValue(tmp2, "IWItemId", new NBTUtil.NBTValue().set("IW_WIRE"));
        ItemMeta meta3 = tmp3.getItemMeta();
        meta3.setDisplayName(ChatColor.WHITE + "导线");
        meta3.setLore(Arrays.asList(
                ChatColor.WHITE + "工业线缆",
                ChatColor.GRAY + "用来在各机器之间传输电力"
        ));
        tmp2.setItemMeta(meta3);
        IW_WIRE = tmp2.clone();
    }
}
