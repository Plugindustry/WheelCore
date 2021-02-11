package com.wheelcore.interfaces.power;

import com.wheelcore.interfaces.block.Wire;

public interface EnergyOutputable {
    boolean finishOutput(Wire.PowerPacket packet);
}
