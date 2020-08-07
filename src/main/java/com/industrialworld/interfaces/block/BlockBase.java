package com.industrialworld.interfaces.block;

import com.industrialworld.interfaces.Interactive;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface BlockBase extends Interactive {
    boolean onBlockPlace(Block block);

    boolean isOre();

    boolean onBlockDestroy(Block block, ItemStack tool, boolean canceled);

    ItemStack getItemStack();

    String getId();

    Material getMaterial();
}
