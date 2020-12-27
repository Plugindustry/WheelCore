package com.industrialworld.block;

import com.industrialworld.interfaces.block.MachineBase;
import com.industrialworld.manager.ItemMapping;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Grinder extends MachineBase {
    @Override
    public ItemStack getItemStack() {
        return ItemMapping.get("GRINDER");
    }

    @Override
    public Material getMaterial() {
        return ItemMapping.get("GRINDER").getType();
    }
}
