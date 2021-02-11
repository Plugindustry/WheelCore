package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface Placeable {
    boolean onBlockPlace(ItemStack item, Block block);
}
