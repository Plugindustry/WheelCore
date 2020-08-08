package com.industrialworld.interfaces.block;

import com.industrialworld.interfaces.Base;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface BlockBase extends Base {
    ItemStack getItemStack();

    String getId();

    Material getMaterial();
}
