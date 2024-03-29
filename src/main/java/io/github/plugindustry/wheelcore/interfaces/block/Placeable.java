package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Placeable extends BlockBase {
    /**
     * @param item         The item that is placed (it may be null when player is null)
     * @param block        The block that is placed
     * @param blockAgainst The block against which the current block is placed (it may be null when player is null)
     * @param player       The player who placed the block (null when the block is not placed by players)
     * @return Whether this place should succeed
     */
    @SuppressWarnings("SameReturnValue")
    boolean onBlockPlace(@Nullable ItemStack item, @Nonnull Block block, @Nullable Block blockAgainst,
            @Nullable Player player);
}