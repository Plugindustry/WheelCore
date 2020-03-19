package com.industrialworld.interfaces;

import com.industrialworld.manager.MainManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public abstract class BlockBase extends Interactive {
    public void onBlockUpdate(Block block) {
    }

    public boolean onBlockPlace(Block block) {
        MainManager.addBlock(MainManager.getIdFromInstance(this), block, null /*currently is null*/);
        return true;
    }

    public boolean isOre() {
        return false;
    }

    public boolean onBlockDestroy(Block block, ItemStack tool, boolean canceled) {
        if (!canceled) {
            MainManager.removeBlock(block);
        }
        // We do not drop IndustrialWorld blocks by default
        block.getWorld().dropItem(block.getLocation(), getItemStack());

        return true;
    }

    public abstract ItemStack getItemStack();

    public abstract String getId();

    public abstract Material getMaterial();
}
