package com.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface Destroyable {
    boolean onBlockDestroy(Block block, ItemStack tool, DestroyMethod method);

    enum DestroyMethod {
        PLAYER_DESTROY, EXPLOSION, OTHER
    }
}
