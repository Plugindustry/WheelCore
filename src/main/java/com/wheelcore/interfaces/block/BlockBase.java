package com.wheelcore.interfaces.block;

import com.wheelcore.interfaces.Base;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface BlockBase extends Base {
    ItemStack getItemStack();

    Material getMaterial();
}
