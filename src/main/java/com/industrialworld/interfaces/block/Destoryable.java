package com.industrialworld.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface Destoryable {
    boolean onBlockDestroy(Block block, ItemStack tool, boolean canceled);
}
