package io.github.plugindustry.wheelcore.interfaces.transport.packet;

import org.bukkit.Location;

import javax.annotation.Nonnull;

public interface Packet extends Cloneable {
    void spread(@Nonnull Location from);
}