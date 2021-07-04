package io.github.plugindustry.wheelcore.interfaces.inventory;

import io.github.plugindustry.wheelcore.inventory.Position;
import org.bukkit.inventory.ItemStack;

public interface WindowInteractor {
    /**
     * @param info The information about the click performed
     * @return Whether this click should be cancelled
     */
    boolean processClick(InventoryClickInfo info);

    /**
     * @param pos  The position where the item is changed
     * @param item What the item at the given position should be changed to
     */
    void onWindowChange(Position pos, ItemStack item);
}
