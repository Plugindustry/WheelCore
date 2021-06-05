package io.github.plugindustry.wheelcore.interfaces.item;

import io.github.plugindustry.wheelcore.interfaces.Interactive;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public abstract class DummyItem implements ItemBase, Interactive {
    @Override
    public boolean onInteract(Player player, Action action, ItemStack tool, Block block) {
        return true;
    }
}
