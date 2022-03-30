package io.github.plugindustry.wheelcore.interfaces;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Interactive {
    /**
     * @param player Who interacted with this
     * @param action The action of this operation
     * @param hand   The used hand of this operation
     * @param tool   The tool the player using
     * @param block  The block the player interacting with
     * @param entity The entity the player interacting with
     * @return Whether this operation shouldn't be cancelled
     */
    boolean onInteract(@Nonnull Player player, @Nonnull Action action, @Nullable EquipmentSlot hand, @Nullable ItemStack tool, @Nullable Block block, @Nullable Entity entity);
}
