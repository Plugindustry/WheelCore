package io.github.plugindustry.wheelcore.interfaces.transport.power;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public interface EnergyOutputable extends BlockBase {
    boolean output(@Nonnull Location block, double amount);
}