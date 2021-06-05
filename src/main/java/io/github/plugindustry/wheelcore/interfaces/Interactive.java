package io.github.plugindustry.wheelcore.interfaces;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public interface Interactive {
    boolean onInteract(Player player, Action action, ItemStack tool, Block block);
}
