package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface Placeable extends ItemBase {
    boolean onItemPlace(@Nonnull ItemStack item, @Nonnull Block block, @Nonnull Block blockAgainst,
            @Nonnull Player player);
}