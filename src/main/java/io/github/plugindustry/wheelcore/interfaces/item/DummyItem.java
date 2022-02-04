package io.github.plugindustry.wheelcore.interfaces.item;

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
import java.util.Objects;
import java.util.Optional;

public class DummyItem implements ItemBase, Interactive, Damageable {
    @Override
    public boolean onInteract(@Nonnull Player player, @Nonnull Action action, @Nullable ItemStack tool, @Nullable Block block, @Nullable Entity entity) {
        return true;
    }

    @Nonnull
    @Override
    public Optional<Integer> onItemDamage(@Nullable Player player, @Nonnull ItemStack item, int damage) {
        return Optional.of(damage);
    }

    @Nonnull
    public ItemStack getItem() {
        return ItemMapping.get(Objects.requireNonNull(MainManager.getIdFromInstance(this)));
    }

    @Nonnull
    @Override
    public Material getMaterial() {
        return getItem().getType();
    }
}
