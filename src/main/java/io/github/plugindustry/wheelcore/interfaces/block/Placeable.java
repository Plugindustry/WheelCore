package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface Placeable {
    boolean onBlockPlace(@Nonnull ItemStack item, @Nonnull Block block, @Nonnull Block blockAgainst, @Nonnull Player player);
}
