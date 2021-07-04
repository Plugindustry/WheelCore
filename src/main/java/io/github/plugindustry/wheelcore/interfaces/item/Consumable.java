package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Consumable {
    boolean onItemConsume(Player player, ItemStack item);
}
