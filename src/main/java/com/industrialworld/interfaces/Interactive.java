package com.industrialworld.interfaces;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public abstract class Interactive extends Base {
    public boolean onInteract(Player player, Action action, ItemStack tool, Block block) {
        return true;
    }
}
