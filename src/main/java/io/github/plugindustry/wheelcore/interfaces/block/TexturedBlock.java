package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface TexturedBlock {
    @Nonnull
    ItemStack getTextureItem(@Nonnull Location location, @Nonnull Player player);
}
