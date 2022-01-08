package io.github.plugindustry.wheelcore.interfaces.block;

import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public abstract class Wire extends DummyBlock {
    /**
     * @return The max value of the energy transferred through this type of wire per tick
     */
    public abstract double getMaxTransmissionEnergy();

    /**
     * @return The loss of the energy transferred through this type of wire per block
     */
    public abstract double getEnergyLoss();

    @Override
    public boolean onBlockPlace(@Nonnull ItemStack item, @Nonnull Block block, @Nonnull Block blockAgainst, @Nonnull Player player) {
        if (super.onBlockPlace(item, block, blockAgainst, player)) {
            MainManager.setBlockData(block.getLocation(), new WireData());
            return true;
        }
        return false;
    }

    public static class WireData extends BlockData {
        public transient double stat = 0.0D;
        public transient double statNext = 0.0D;
        public transient List<PowerPacket> packets = new LinkedList<>();
        public transient List<PowerPacket> nextPackets = new LinkedList<>();
    }

    public static class PowerPacket implements Cloneable {
        public Location src;
        public Location from;
        public double amount;
        public double orgAmount;

        public PowerPacket(Location src, double amount) {
            this.src = src;
            this.from = src;
            this.amount = amount;
            this.orgAmount = amount;
        }

        public PowerPacket clone() {
            try {
                PowerPacket clone = (PowerPacket) super.clone();
                clone.src = src;
                clone.from = from;
                clone.amount = amount;
                clone.orgAmount = orgAmount;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }
}