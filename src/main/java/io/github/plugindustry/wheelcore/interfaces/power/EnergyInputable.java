package io.github.plugindustry.wheelcore.interfaces.power;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.world.packet.Packet;
import io.github.plugindustry.wheelcore.interfaces.world.packet.PacketConsumer;
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
            if (out.output(packet.src, fin)) {
                packet.amount -= fin;
                input(loc, fin);
            }
        }
    }

    double demand(@Nonnull Location block);

    void input(@Nonnull Location block, double amount);
}