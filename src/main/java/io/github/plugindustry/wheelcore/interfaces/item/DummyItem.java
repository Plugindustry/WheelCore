package io.github.plugindustry.wheelcore.interfaces.item;

import io.github.plugindustry.wheelcore.interfaces.Interactive;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DummyItem implements ItemBase, Interactive {
    @Override
    public boolean onInteract(@Nonnull Player player, @Nonnull Action action, @Nullable ItemStack tool, @Nullable Block block, @Nullable Entity entity) {
        return true;
    }
}
