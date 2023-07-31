package io.github.plugindustry.wheelcore.interfaces.transport.power;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.Packet;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.PacketConsumer;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EnergyInputable extends BlockBase, PacketConsumer<EnergyPacket> {
    default boolean canAccept(@Nullable Packet packet) {
        return packet instanceof EnergyPacket;
    }

    default void accept(@Nonnull Location loc, @Nonnull EnergyPacket packet) {
        if (MainManager.getBlockInstance(packet.src) instanceof EnergyOutputable out) {
            double fin = Math.min(packet.amount, demand(loc));
            double orgFin = packet.orgAmount / packet.amount * fin;
            if (out.output(packet.src, orgFin)) {
                packet.amount -= fin;
                packet.orgAmount -= orgFin;
                input(loc, fin);
            }
        }
    }

    double demand(@Nonnull Location block);

    void input(@Nonnull Location block, double amount);
}