package com.industrialworld.item.template;

import com.industrialworld.item.material.IWMaterial;
import com.industrialworld.item.material.info.MaterialInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemTemplate {
    public static void register(IWMaterial iwMaterial, MaterialInfo materialInfo) { }

    public static ItemStack getItemStack(IWMaterial iwMaterial) {
        return new ItemStack(Material.AIR);
    }
}
