package io.github.plugindustry.wheelcore.inventory;

import io.github.plugindustry.wheelcore.interfaces.inventory.InventoryClickInfo;
import org.bukkit.inventory.ItemStack;

public interface WindowInteractor {
    boolean processClick(InventoryClickInfo info);

    void onWindowChange(Position pos, ItemStack item);
}
