package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Destroyable {
    boolean onBlockDestroy(@Nonnull Block block, @Nullable ItemStack tool, @Nonnull DestroyMethod method);

    enum DestroyMethod {
        PLAYER_DESTROY, EXPLOSION, OTHER
    }
}
