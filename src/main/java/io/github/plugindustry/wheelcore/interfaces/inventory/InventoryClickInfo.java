package io.github.plugindustry.wheelcore.interfaces.inventory;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class InventoryClickInfo {
    public final ClickType click;
    public final InventoryAction action;
    public final InventoryType.SlotType slotType;
    public final int slot;
    public final int rawSlot;
    public final ItemStack current;
    public final int hotbarKey;

    public InventoryClickInfo(InventoryClickEvent event) {
        click = event.getClick();
        action = event.getAction();
        slotType = event.getSlotType();
        slot = event.getSlot();
        rawSlot = event.getRawSlot();
        current = event.getCurrentItem();
        hotbarKey = event.getHotbarButton();
    }
}
