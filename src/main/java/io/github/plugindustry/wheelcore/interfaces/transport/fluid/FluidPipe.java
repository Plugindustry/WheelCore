package io.github.plugindustry.wheelcore.interfaces.transport.fluid;

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

public abstract class FluidPipe extends DummyBlock implements PacketContainer<FluidPacket>, Tickable {
    public abstract int getThreshold();

    public abstract int getSpeed();

    @Nullable
    @Override
    public BlockData getInitialData(@Nullable ItemStack item, @Nonnull Block block, @Nullable Block blockAgainst,
            @Nullable Player player) {
        return new PipeData();
    }

    @Override
    @Nonnull
    public List<FluidPacket> packets(@Nonnull Location loc) {
        return Collections.unmodifiableList(((PipeData) Objects.requireNonNull(MainManager.getBlockData(loc))).packets);
    }

    @Override
    public boolean available(@Nonnull Location loc) {
        return ((PipeData) Objects.requireNonNull(MainManager.getBlockData(loc))).stat < getThreshold();
    }

    @Override
    public void add(@Nonnull Location loc, @Nonnull FluidPacket packet) {
        if (packet.fluid.getAmount() <= 0) return;
        PipeData data = (PipeData) Objects.requireNonNull(MainManager.getBlockData(loc));
        int ava = getThreshold() - data.stat;
        if (ava > 0) {
            if (ava < packet.fluid.getAmount()) packet.fluid.setAmount(packet.fluid.getAmount() - ava);
            else {
                ava = packet.fluid.getAmount();
                packet.fluid.setAmount(0);
            }
            data.stat += ava;
            FluidPacket tmp = packet.clone();
            tmp.fluid.setAmount(ava);
            data.packets.add(tmp);
        }
    }

    @Override
    public void spread(@Nonnull Location loc) {
        PipeData data = (PipeData) Objects.requireNonNull(MainManager.getBlockData(loc));
        if (data.packets.isEmpty()) return;

        FluidPacket packet = data.packets.get(data.packets.size() - 1);
        data.packets.remove(data.packets.size() - 1);
        data.stat -= packet.fluid.getAmount();
        Location loc2 = loc.clone();
        MainManager.queuePostTickTask(() -> packet.spread(loc2));
    }

    @Override
    public void spreadAll(@Nonnull Location loc) {
        PipeData data = (PipeData) Objects.requireNonNull(MainManager.getBlockData(loc));
        Location loc2 = loc.clone();
        data.packets.forEach(packet -> MainManager.queuePostTickTask(() -> packet.spread(loc2)));
        data.packets.clear();
        data.stat = 0;
    }

    @Override
    public boolean canAccept(@Nullable Packet packet) {
        return packet instanceof FluidPacket;
    }

    @Override
    public void onTick() {
        MainManager.blockDataProvider.blocksOf(this).forEach(block -> {
            if (MainManager.getBlockData(block) instanceof PipeData data) if (data.tickCount >= getSpeed()) {
                data.tickCount = 0;
                spreadAll(block);
            } else ++data.tickCount;
        });
    }

    public static class PipeData extends BlockData {
        public final ArrayList<FluidPacket> packets = new ArrayList<>();
        public int stat = 0;

        public transient int tickCount = 0;
    }
}