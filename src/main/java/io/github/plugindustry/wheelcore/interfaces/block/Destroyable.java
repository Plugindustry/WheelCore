package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface Destroyable extends BlockBase {
    boolean onBlockDestroy(@Nonnull Block block, @Nonnull DestroyMethod method, @Nullable ItemStack tool,
            @Nullable Player player);

    float getHardness(@Nonnull Block block);

    boolean isPreferredTool(@Nonnull Block block, @Nonnull ItemStack tool);

    boolean needCorrectTool(@Nonnull Block block);

    /**
     * @return If the optional is empty, vanilla explosion resistance is used.
     * <p>
     * Otherwise, the float value inside is the explosion resistance.
     */
    Optional<Float> getBlastResistance(@Nonnull Block block);

    enum DestroyMethod {
        PLAYER_DESTROY, EXPLOSION, PHYSICS, FIRE, FADE, DECAY, OTHER
    }
}