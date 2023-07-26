package io.github.plugindustry.wheelcore.interfaces.transport.item;

import io.github.plugindustry.wheelcore.interfaces.transport.packet.RandomSpreadPacket;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ItemPacket extends RandomSpreadPacket {
    public ItemStack item;

    public ItemPacket() {}

    public ItemPacket(Location src, ItemStack item) {
        super(src);
        this.item = item.clone();
    }

    public ItemPacket clone() {
        ItemPacket clone = (ItemPacket) super.clone();
        clone.item = item.clone();
        return clone;
    }
}