package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface Damageable extends ItemBase {
    /**
     * @param player The player whose item is being damaged, or null if this damage is not caused by players' behaviors
     * @param item   The item being damaged
     * @param damage The amount of damage being applied
     * @return The amount of damage that was actually applied, or empty if the event should be cancelled
     */
    @Nonnull
    Optional<Integer> onItemDamage(@Nullable Player player, @Nonnull ItemStack item, int damage);
}