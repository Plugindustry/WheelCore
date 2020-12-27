package com.industrialworld.block;

import com.industrialworld.ConstItems;
import com.industrialworld.interfaces.block.DummyBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicMachineBlock extends DummyBlock {
    @Override
    public ItemStack getItemStack() {
        return ConstItems.BASIC_MACHINE_BLOCK;
    }

    @Override
    public Material getMaterial() {
        return ConstItems.BASIC_MACHINE_BLOCK.getType();
    }
}
