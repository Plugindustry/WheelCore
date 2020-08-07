package com.industrialworld.interfaces.power;

import com.industrialworld.interfaces.block.Wire;

public interface EnergyOutputable {
    boolean finishOutput(Wire.PowerPacket packet);
}
