package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import java.util.List;

public interface PistonPullable {
    /**
     * @param block        The pulled block
     * @param piston       The piston that is pulling the block
     * @param direction    The direction in which the piston is pulling the block
     * @param pulledBlocks The list of blocks that are being pulled
     * @return Whether the block should be pulled
     */
    boolean onPistonPull(@Nonnull Block block, @Nonnull Block piston, @Nonnull BlockFace direction,
                         @Nonnull List<Block> pulledBlocks);
}
