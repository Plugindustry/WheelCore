package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface Consumable extends ItemBase {
    boolean onItemConsume(@Nonnull Player player, @Nonnull ItemStack item);
}