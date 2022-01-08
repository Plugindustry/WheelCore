package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface Breakable {
    void onItemBreak(@Nonnull Player player, @Nonnull ItemStack item);
}
