package io.github.plugindustry.wheelcore.interfaces.power;

import io.github.plugindustry.wheelcore.interfaces.block.Wire;

import javax.annotation.Nonnull;

public interface EnergyInputable {
    void finishInput(@Nonnull Wire.PowerPacket packet);
}
