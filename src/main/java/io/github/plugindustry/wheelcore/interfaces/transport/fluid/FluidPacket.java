package io.github.plugindustry.wheelcore.interfaces.transport.fluid;

import io.github.plugindustry.wheelcore.interfaces.fluid.FluidStack;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.RandomSpreadPacket;
import org.bukkit.Location;

public class FluidPacket extends RandomSpreadPacket {
    public FluidStack fluid;

    public FluidPacket() {}

    public FluidPacket(Location src, FluidStack fluid) {
        super(src);
        this.fluid = fluid.clone();
    }

    public FluidPacket clone() {
        FluidPacket clone = (FluidPacket) super.clone();
        clone.fluid = fluid.clone();
        return clone;
    }
}