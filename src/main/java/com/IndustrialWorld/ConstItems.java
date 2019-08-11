package com.IndustrialWorld;

import com.IndustrialWorld.utils.NBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class ConstItems {
    public final static ItemStack BASIC_MACHINE_BLOCK;

    static {
        ItemStack tmp1 = new ItemStack(Material.IRON_BLOCK, 1);
        tmp1 = NBTUtil.setTagValue(tmp1, "isIWItem", new NBTUtil.NBTValue().set(true));
        tmp1 = NBTUtil.setTagValue(tmp1, "IWItemId", new NBTUtil.NBTValue().set("BASIC_MACHINE_BLOCK"));
        ItemMeta meta = tmp1.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "基础机器方块");
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "最基本的机器外壳",
                ChatColor.GRAY + "可用于制造基础机器"
        ));
        tmp1.setItemMeta(meta);
        BASIC_MACHINE_BLOCK = tmp1.clone();
    }
}
