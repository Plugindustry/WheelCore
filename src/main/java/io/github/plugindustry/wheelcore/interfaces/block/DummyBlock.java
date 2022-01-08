package io.github.plugindustry.wheelcore.interfaces.block;

import io.github.plugindustry.wheelcore.interfaces.Interactive;
import io.github.plugindustry.wheelcore.manager.ItemMapping;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DummyBlock implements BlockBase, Placeable, Destroyable, Interactive {
    @Override
    public boolean onInteract(@Nonnull Player player, @Nonnull Action action, ItemStack tool, Block block, Entity entity) {
        return true;
    }

    @Override
    public boolean onBlockPlace(@Nonnull ItemStack item, @Nonnull Block block, @Nonnull Block blockAgainst, @Nonnull Player player) {
        // We do nothing by default, so you should do this job in your implementation too.
        MainManager.addBlock(block.getLocation(), this, null);
        if (getMaterial() != item.getType())
            block.setType(getMaterial());
        return true;
    }

    @Override
    public boolean onBlockDestroy(@Nonnull Block block, @Nonnull DestroyMethod method, ItemStack tool, @Nullable Player player) {
        // We do nothing by default, so you should do this job in your implementation too.
        MainManager.removeBlock(block.getLocation());
        block.getWorld().dropItem(block.getLocation(), getItemStack());
        return true;
    }

    @Nonnull
    public ItemStack getItemStack() {
        return ItemMapping.get(MainManager.getIdFromInstance(this));
    }

    @Override
    @Nonnull
    public Material getMaterial() {
        return getItemStack().getType();
    }
}
