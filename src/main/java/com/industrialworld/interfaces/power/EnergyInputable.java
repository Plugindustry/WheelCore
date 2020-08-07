package com.industrialworld.interfaces.power;

import com.industrialworld.interfaces.block.Wire;

public interface EnergyInputable {
    void finishInput(Wire.PowerPacket packet);
}
