package io.github.plugindustry.wheelcore.interfaces.power;

import io.github.plugindustry.wheelcore.interfaces.block.Wire;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public interface EnergyInputable {
    void finishInput(@Nonnull Location block, @Nonnull Wire.PowerPacket packet);
}
