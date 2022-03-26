package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import java.util.List;

public interface PistonPushable {
    /**
     * @param block        The pushed block
     * @param piston       The piston that is pushing the block
     * @param direction    The direction in which the piston is pushing the block
     * @param pushedBlocks The list of blocks that are being pushed
     * @return Whether the block should be pushed
     */
    boolean onPistonPush(@Nonnull Block block, @Nonnull Block piston, @Nonnull BlockFace direction, @Nonnull List<Block> pushedBlocks);
}
