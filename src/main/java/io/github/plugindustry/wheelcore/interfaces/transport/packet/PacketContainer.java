package io.github.plugindustry.wheelcore.interfaces.transport.packet;

import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.List;

public interface PacketContainer<T extends Packet> extends PacketConsumer<T> {
    @Nonnull
    List<T> packets(@Nonnull Location loc);

    void add(@Nonnull Location loc, @Nonnull T packet);

    void spread(@Nonnull Location loc);

    void spreadAll(@Nonnull Location loc);

    default void accept(@Nonnull Location loc, @Nonnull T packet) {
        add(loc, packet);
    }
}