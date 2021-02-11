package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Wire extends DummyBlock {
    public abstract double getMaxTransmissionEnergy();

    public abstract double getEnergyLoss();

    public static class WireData extends BlockData {
        public double stat = 0.0D;
        public double statNext = 0.0D;
        public List<PowerPacket> packets = new LinkedList<>();
        public List<PowerPacket> nextPackets = new LinkedList<>();

        @Nonnull
        @Override
        public Map<String, Object> serialize() {
            TreeMap<String, Object> map = new TreeMap<>();
            map.put("packets", packets);
            return map;
        }
    }

    public static class PowerPacket implements ConfigurationSerializable {
        public Location src;
        public Location from;
        public double amount;

        public PowerPacket(Location src, double amount) {
            this.src = src;
            this.from = src;
            this.amount = amount;
        }

        public PowerPacket clone() {
            try {
                PowerPacket clone = (PowerPacket) super.clone();
                clone.src = src;
                clone.from = from;
                clone.amount = amount;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Nonnull
        @Override
        public Map<String, Object> serialize() {
            TreeMap<String, Object> map = new TreeMap<>();
            map.put("src", src);
            map.put("from", from);
            map.put("amount", amount);
            return map;
        }
    }
}