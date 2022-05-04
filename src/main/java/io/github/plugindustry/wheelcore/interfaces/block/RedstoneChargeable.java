package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.block.Block;

import javax.annotation.Nonnull;

public interface RedstoneChargeable extends BlockBase {
    /**
     * @param block    The charged block
     * @param oldPower The old power level
     * @param newPower The new power level
     */
    void onRedstone(@Nonnull Block block, int oldPower, int newPower);
}