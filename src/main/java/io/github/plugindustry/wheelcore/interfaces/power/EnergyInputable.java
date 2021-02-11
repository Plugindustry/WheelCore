package io.github.plugindustry.wheelcore.interfaces.power;

import io.github.plugindustry.wheelcore.interfaces.block.Wire;

public interface EnergyInputable {
    void finishInput(Wire.PowerPacket packet);
}
