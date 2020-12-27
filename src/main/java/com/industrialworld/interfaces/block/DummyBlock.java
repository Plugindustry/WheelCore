package com.industrialworld.interfaces.block;

import com.industrialworld.interfaces.Interactive;
import com.industrialworld.interfaces.Tickable;
import com.industrialworld.manager.MainManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public abstract class DummyBlock implements BlockBase, Tickable, Placeable, Destroyable, Interactive {
    @Override
    public void onTick() {
        // Do nothing.
    }

    @Override
    public boolean onInteract(Player player, Action action, ItemStack tool, Block block, InteractActor actor) {
        return true;
    }

    public boolean onBlockPlace(ItemStack item, Block block) {
        MainManager.addBlock(block.getLocation(), this, null /*currently is null*/);
        return true;
    }

    public boolean onBlockDestroy(Block block, ItemStack tool, DestroyMethod method) {
        MainManager.removeBlock(block.getLocation());
        // We do not drop IndustrialWorld blocks by default
        block.getWorld().dropItem(block.getLocation(), getItemStack());

        return true;
    }

    public abstract ItemStack getItemStack();

    public abstract Material getMaterial();
}
