package io.github.plugindustry.wheelcore.interfaces.transport.power;

import io.github.plugindustry.wheelcore.interfaces.transport.packet.RandomSpreadPacket;
import org.bukkit.Location;

public class EnergyPacket extends RandomSpreadPacket {
    public double orgAmount;
    public double amount;

    public EnergyPacket() {}

    public EnergyPacket(Location src, double amount) {
        super(src);
        this.orgAmount = amount;
        this.amount = amount;
    }

    public EnergyPacket clone() {
        EnergyPacket clone = (EnergyPacket) super.clone();
        clone.orgAmount = orgAmount;
        clone.amount = amount;
        return clone;
    }
}