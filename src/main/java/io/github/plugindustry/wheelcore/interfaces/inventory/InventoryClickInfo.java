package io.github.plugindustry.wheelcore.interfaces.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickInfo {
    public final Inventory inventory;
    public final ClickType click;
    public final InventoryAction action;
    public final InventoryType.SlotType slotType;
    public final int slot;
    public final int rawSlot;
    public final ItemStack current;
    public final int hotbarKey;
    public final HumanEntity whoClicked;

    public InventoryClickInfo(InventoryClickEvent event) {
        inventory = event.getInventory();
        click = event.getClick();
        action = event.getAction();
        slotType = event.getSlotType();
        slot = event.getSlot();
        rawSlot = event.getRawSlot();
        current = event.getCurrentItem();
        hotbarKey = event.getHotbarButton();
        whoClicked = event.getWhoClicked();
    }
}
