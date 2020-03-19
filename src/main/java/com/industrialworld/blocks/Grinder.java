package com.industrialworld.blocks;

import com.industrialworld.interfaces.MachineBase;
import com.industrialworld.manager.ItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Grinder extends MachineBase {
    @Override
    public ItemStack getItemStack() {
        return ItemManager.get("GRINDER");
    }

    @Override
    public String getId() {
        return "GRINDER";
    }

    @Override
    public Material getMaterial() {
        return ItemManager.get("GRINDER").getType();
    }
}
