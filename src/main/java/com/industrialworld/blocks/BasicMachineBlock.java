package com.industrialworld.blocks;

import com.industrialworld.ConstItems;
import com.industrialworld.interfaces.BlockBase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicMachineBlock extends BlockBase {
    @Override
    public ItemStack getItemStack() {
        return ConstItems.BASIC_MACHINE_BLOCK;
    }

    @Override
    public String getId() {
        return "MachineBlock";
    }

    @Override
    public Material getMaterial() {
        return ConstItems.BASIC_MACHINE_BLOCK.getType();
    }
}
