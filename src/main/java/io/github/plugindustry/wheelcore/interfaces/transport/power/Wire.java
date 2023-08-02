package io.github.plugindustry.wheelcore.interfaces.transport.power;

import io.github.plugindustry.wheelcore.interfaces.Tickable;
import io.github.plugindustry.wheelcore.interfaces.block.BlockData;
import io.github.plugindustry.wheelcore.interfaces.block.DummyBlock;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.Packet;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.PacketContainer;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Wire extends DummyBlock implements PacketContainer<EnergyPacket>, Tickable {
    /**
     * @return The max value of the energy transferred through this type of wire per tick
     */
    public abstract double getMaxTransmissionEnergy();

    /**
     * @return The loss of the energy transferred through this type of wire per block
     */
    public abstract double getEnergyLoss();

    @Nullable
    @Override
    public BlockData getInitialData(@Nullable ItemStack item, @Nonnull Block block, @Nullable Block blockAgainst,
            @Nullable Player player) {
        return new WireData();
    }

    @Override
    @Nonnull
    public List<EnergyPacket> packets(@Nonnull Location loc) {
        return Collections.unmodifiableList(((WireData) Objects.requireNonNull(MainManager.getBlockData(loc))).packets);
    }

    @Override
    public boolean available(@Nonnull Location loc) {
        return ((WireData) Objects.requireNonNull(MainManager.getBlockData(loc))).stat < getMaxTransmissionEnergy();
    }

    @Override
    public void add(@Nonnull Location loc, @Nonnull EnergyPacket packet) {
        if (packet.amount <= 0) return;
        WireData data = (WireData) Objects.requireNonNull(MainManager.getBlockData(loc));
        double ava = getMaxTransmissionEnergy() - data.stat;
        if (ava > 0) {
            if (ava < packet.amount) packet.amount -= ava;
            else {
                ava = packet.amount;
                packet.amount = 0;
            }
            data.stat += ava;
            EnergyPacket tmp = packet.clone();
            tmp.amount = ava;
            data.packets.add(tmp);
        }
    }

    @Override
    public void spread(@Nonnull Location loc) {
        WireData data = (WireData) Objects.requireNonNull(MainManager.getBlockData(loc));
        if (data.packets.isEmpty()) return;

        EnergyPacket packet = data.packets.get(data.packets.size() - 1);
        data.packets.remove(data.packets.size() - 1);
        data.stat -= packet.amount;
        Location loc2 = loc.clone();
        if (packet.amount > getEnergyLoss()) {
            packet.amount -= getEnergyLoss();
            MainManager.queuePostTickTask(() -> packet.spread(loc2));
        }
    }

    @Override
    public void spreadAll(@Nonnull Location loc) {
        WireData data = (WireData) Objects.requireNonNull(MainManager.getBlockData(loc));
        Location loc2 = loc.clone();
        data.packets.forEach(packet -> {
            if (packet.amount > getEnergyLoss()) {
                packet.amount -= getEnergyLoss();
                MainManager.queuePostTickTask(() -> packet.spread(loc2));
            }
        });
        data.packets.clear();
        data.stat = 0;
    }

    @Override
    public boolean canAccept(@Nullable Packet packet) {
        return packet instanceof EnergyPacket;
    }

    @Override
    public void onTick() {
        MainManager.blockDataProvider.blocksOf(this).forEach(this::spreadAll);
    }

    public static class WireData extends BlockData {
        public transient double stat = 0.0D;
        public final transient ArrayList<EnergyPacket> packets = new ArrayList<>();
    }
}