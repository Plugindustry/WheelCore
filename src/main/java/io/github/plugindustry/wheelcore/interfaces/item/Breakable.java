package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Breakable {
    void onItemBreak(Player player, ItemStack item);
}
