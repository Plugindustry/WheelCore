package io.github.plugindustry.wheelcore.interfaces.transport.fluid;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.fluid.FluidBase;
import io.github.plugindustry.wheelcore.interfaces.fluid.FluidStack;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.Packet;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.PacketConsumer;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface FluidInputable extends BlockBase, PacketConsumer<FluidPacket> {
    default boolean canAccept(@Nullable Packet packet) {
        return packet instanceof FluidPacket;
    }

    default void accept(@Nonnull Location loc, @Nonnull FluidPacket packet) {
        if (MainManager.getBlockInstance(packet.src) instanceof FluidOutputable out) {
            int fin = Math.min(packet.fluid.getAmount(), demand(loc, packet.fluid.getType()));
            FluidStack fluid = packet.fluid.clone();
            fluid.setAmount(fin);
            if (out.output(packet.src, fluid)) {
                packet.fluid.setAmount(packet.fluid.getAmount() - fin);
                input(loc, fluid);
            }
        }
    }

    int demand(@Nonnull Location block, @Nonnull FluidBase type);

    void input(@Nonnull Location block, @Nonnull FluidStack fluid);
}