package io.github.plugindustry.wheelcore.interfaces.block;

import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;

public abstract class Wire extends DummyBlock {
    public abstract double getMaxTransmissionEnergy();

    public abstract double getEnergyLoss();

    public static class WireData extends BlockData {
        public transient double stat = 0.0D;
        public transient double statNext = 0.0D;
        public transient List<PowerPacket> packets = new LinkedList<>();
        public transient List<PowerPacket> nextPackets = new LinkedList<>();
    }

    public static class PowerPacket {
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
    }
}