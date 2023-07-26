package io.github.plugindustry.wheelcore.interfaces.transport.packet;

import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.BlockUtil;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RandomSpreadPacket implements Packet {
    public Location src;
    public Location from;

    public RandomSpreadPacket() {}

    public RandomSpreadPacket(Location src) {
        this.src = src.clone();
        this.from = src.clone();
    }

    public RandomSpreadPacket clone() {
        try {
            RandomSpreadPacket clone = (RandomSpreadPacket) super.clone();
            clone.src = src.clone();
            clone.from = from.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void spread(@Nonnull Location from) {
        List<Location> list = BlockUtil.findAcceptableAround(from, this).filter(loc -> !loc.equals(this.from))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(list);
        this.from = from;
        list.forEach(location -> {
            PacketConsumer pc = (PacketConsumer) Objects.requireNonNull(MainManager.getBlockInstance(location));
            pc.accept(location, this);
        });
    }
}