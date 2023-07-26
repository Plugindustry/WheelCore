package io.github.plugindustry.wheelcore.interfaces.transport.item;

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

public abstract class ItemPipe extends DummyBlock implements PacketContainer<ItemPacket>, Tickable {
    public abstract int getThreshold();

    @Nullable
    @Override
    public BlockData getInitialData(@Nullable ItemStack item, @Nonnull Block block, @Nullable Block blockAgainst,
            @Nullable Player player) {
        return new PipeData();
    }

    @Override
    @Nonnull
    public List<ItemPacket> packets(@Nonnull Location loc) {
        return Collections.unmodifiableList(((PipeData) Objects.requireNonNull(MainManager.getBlockData(loc))).packets);
    }

    @Override
    public boolean available(@Nonnull Location loc) {
        return ((PipeData) Objects.requireNonNull(MainManager.getBlockData(loc))).stat < getThreshold();
    }

    @Override
    public void add(@Nonnull Location loc, @Nonnull ItemPacket packet) {
        PipeData data = (PipeData) Objects.requireNonNull(MainManager.getBlockData(loc));
        int ava = getThreshold() - data.stat;
        if (ava > 0) {
            if (ava < packet.item.getAmount()) packet.item.setAmount(packet.item.getAmount() - ava);
            else {
                ava = packet.item.getAmount();
                packet.item.setAmount(0);
            }
            data.stat += ava;
            ItemPacket tmp = packet.clone();
            tmp.item.setAmount(ava);
            data.packets.add(tmp);
        }
    }

    @Override
    public void spread(@Nonnull Location loc) {
        PipeData data = (PipeData) Objects.requireNonNull(MainManager.getBlockData(loc));
        if (data.packets.isEmpty()) return;

        ItemPacket packet = data.packets.get(data.packets.size() - 1);
        data.packets.remove(data.packets.size() - 1);
        data.stat -= packet.item.getAmount();
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
        return packet instanceof ItemPacket;
    }

    @Override
    public void onTick() {
        MainManager.blockDataProvider.blocksOf(this).forEach(this::spreadAll);
    }

    public static class PipeData extends BlockData {
        public final ArrayList<ItemPacket> packets = new ArrayList<>();
        public int stat = 0;
    }
}