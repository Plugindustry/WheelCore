package io.github.plugindustry.wheelcore.interfaces.block;

import io.github.plugindustry.wheelcore.interfaces.Interactive;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class DummyBlock implements BlockBase, Placeable, Destroyable, Interactive {
    @Override
    public boolean onInteract(@Nonnull Player player, @Nonnull Action action, ItemStack tool, Block block) {
        return true;
    }

    @Override
    public boolean onBlockPlace(@Nonnull ItemStack item, @Nonnull Block block) {
        MainManager.addBlock(block.getLocation(), this, null);
        if (getMaterial() != item.getType())
            block.setType(getMaterial());
        return true;
    }

    @Override
    public boolean onBlockDestroy(@Nonnull Block block, ItemStack tool, @Nonnull DestroyMethod method) {
        // We do nothing by default so you should do this job in your implementation too.
        MainManager.removeBlock(block.getLocation());
        block.getWorld().dropItem(block.getLocation(), getItemStack());
        return true;
    }

    @Nonnull
    public abstract ItemStack getItemStack();

    @Override
    @Nonnull
    public abstract Material getMaterial();
}
