package com.czm.IndustrialWorld;

import com.czm.IndustrialWorld.utils.NBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class ConstItems {
    public final static ItemStack BASIC_MACHINE_BLOCK = new ItemStack(Material.IRON_BLOCK, 1);

    static {
        NBTUtil.setTagValue(BASIC_MACHINE_BLOCK, "isIWItem", new NBTUtil.NBTValue().set(true));
        NBTUtil.setTagValue(BASIC_MACHINE_BLOCK, "IWItemId", new NBTUtil.NBTValue().set("BASIC_MACHINE_BLOCK"));
        ItemMeta meta = BASIC_MACHINE_BLOCK.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "基础机器方块");
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "最基本的机器外壳",
                ChatColor.GRAY + "可用于制造基础机器"
        ));
        BASIC_MACHINE_BLOCK.setItemMeta(meta);
    }
}
