package io.github.plugindustry.wheelcore.interfaces.transport.packet;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PacketConsumer<T extends Packet> extends BlockBase {
    boolean canAccept(@Nullable Packet packet);

    boolean available(@Nonnull Location loc);

    void accept(@Nonnull Location loc, @Nonnull T packet);
}