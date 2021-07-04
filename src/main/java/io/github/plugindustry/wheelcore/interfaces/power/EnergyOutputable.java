package io.github.plugindustry.wheelcore.interfaces.power;

import io.github.plugindustry.wheelcore.interfaces.block.Wire;

import javax.annotation.Nonnull;

public interface EnergyOutputable {
    boolean finishOutput(@Nonnull Wire.PowerPacket packet);
}
