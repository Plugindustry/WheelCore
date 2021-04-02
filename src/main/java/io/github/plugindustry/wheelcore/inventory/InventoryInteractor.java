package io.github.plugindustry.wheelcore.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface InventoryInteractor {
    void renderInventory();

    boolean processClick(int slot, InventoryClickEvent event);
}
