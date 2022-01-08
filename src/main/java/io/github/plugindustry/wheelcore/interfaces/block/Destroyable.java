package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Destroyable {
    boolean onBlockDestroy(@Nonnull Block block, @Nonnull DestroyMethod method, @Nullable ItemStack tool, @Nullable Player player);

    enum DestroyMethod {
        PLAYER_DESTROY, EXPLOSION, OTHER
    }
}
