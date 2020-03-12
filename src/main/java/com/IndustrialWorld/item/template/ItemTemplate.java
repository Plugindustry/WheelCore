package com.IndustrialWorld.item.template;

import com.IndustrialWorld.item.material.IWMaterial;
import com.IndustrialWorld.item.material.info.MaterialInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ItemTemplate {
    public static void register(IWMaterial iwMaterial, MaterialInfo materialInfo) { }

    public static ItemStack getItemStack(IWMaterial iwMaterial) {
        return new ItemStack(Material.AIR);
    }
}
