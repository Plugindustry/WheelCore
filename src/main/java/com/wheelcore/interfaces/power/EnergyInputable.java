package com.wheelcore.interfaces.power;

import com.wheelcore.interfaces.block.Wire;

public interface EnergyInputable {
    void finishInput(Wire.PowerPacket packet);
}
