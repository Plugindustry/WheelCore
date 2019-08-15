package com.IndustrialWorld.interfaces;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public interface InventoryListener {
    void onInventoryClick(InventoryClickEvent event);

    void onInventoryClose(InventoryCloseEvent event);
}
