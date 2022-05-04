package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockIgniteEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Ignitable extends BlockBase {
    /**
     * @param block          The ignited block
     * @param cause          The cause of the ignition
     * @param ignitingBlock  The block that is igniting the block
     * @param ignitingEntity The entity that is igniting the block
     * @return Whether the block should be ignited
     */
    boolean onIgnite(@Nonnull Block block, BlockIgniteEvent.IgniteCause cause, @Nullable Block ignitingBlock,
            @Nullable Entity ignitingEntity);
}