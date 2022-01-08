package io.github.plugindustry.wheelcore.interfaces.item;

import io.github.plugindustry.wheelcore.interfaces.Interactive;
import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DummyItem implements ItemBase, Interactive, Placeable {
    @Override
    public boolean onInteract(@Nonnull Player player, @Nonnull Action action, @Nullable ItemStack tool, @Nullable Block block, Entity entity) {
        return true;
    }

    @Override
    public boolean onItemPlace(@Nonnull ItemStack item, @Nonnull Block block, @Nonnull Block blockAgainst, @Nonnull Player player) {
        // We do nothing by default, so you should do this job in your implementation too.
        BlockBase blockBase = getBlock();
        return blockBase instanceof io.github.plugindustry.wheelcore.interfaces.block.Placeable &&
               ((io.github.plugindustry.wheelcore.interfaces.block.Placeable) blockBase).onBlockPlace(item,
                                                                                                      block,
                                                                                                      blockAgainst,
                                                                                                      player);
    }

    public BlockBase getBlock() {
        return MainManager.getBlockInstanceFromId(MainManager.getIdFromInstance(this));
    }
}
