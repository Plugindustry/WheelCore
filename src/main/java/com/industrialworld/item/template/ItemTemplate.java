package com.industrialworld.item.template;

import com.industrialworld.item.material.IWMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemTemplate {
    private static ItemTemplate instance = new ItemTemplate();

    protected ItemTemplate() {
    }

    public static ItemTemplate getInstance() {
        return instance;
    }

    public void register(IWMaterial iwMaterial) {
    }


    public ItemStack getItemStack(IWMaterial iwMaterial, int amount) {
        return new ItemStack(Material.AIR);
    }

    public ItemStack getItemStack(IWMaterial iwMaterial) {
        return new ItemStack(Material.AIR);
    }
}
