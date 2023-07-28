package io.github.plugindustry.wheelcore.interfaces.transport.fluid;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.fluid.FluidStack;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public interface FluidOutputable extends BlockBase {
    boolean output(@Nonnull Location block, @Nonnull FluidStack fluid);
}