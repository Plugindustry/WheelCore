package io.github.plugindustry.wheelcore.interfaces.power;

import io.github.plugindustry.wheelcore.interfaces.block.Wire;

public interface EnergyOutputable {
    boolean finishOutput(Wire.PowerPacket packet);
}
