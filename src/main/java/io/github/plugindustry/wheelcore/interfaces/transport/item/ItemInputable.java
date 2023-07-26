package io.github.plugindustry.wheelcore.interfaces.transport.item;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.Packet;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.PacketConsumer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface ItemInputable extends BlockBase, PacketConsumer<ItemPacket> {
    default boolean canAccept(@Nullable Packet packet) {
        return packet instanceof ItemPacket;
    }

    default void accept(@Nonnull Location loc, @Nonnull ItemPacket packet) {
        if (demand(loc).test(packet.item)) input(loc, packet.item);
    }

    Predicate<ItemStack> demand(@Nonnull Location block);

    void input(@Nonnull Location block, @Nonnull ItemStack item);
}