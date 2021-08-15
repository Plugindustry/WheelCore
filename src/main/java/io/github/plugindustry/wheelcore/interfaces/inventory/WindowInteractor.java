package io.github.plugindustry.wheelcore.interfaces.inventory;

import io.github.plugindustry.wheelcore.inventory.Position;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public interface WindowInteractor {
    /**
     * @param info The information about the click performed
     * @return Whether this click should be cancelled
     */
    boolean processClick(InventoryClickInfo info);

    /**
     * @param whoClosed Who closed the window
     */
    void processClose(HumanEntity whoClosed);

    /**
     * @param pos  The position where the item is changed
     * @param item What the item at the given position should be changed to
     */
    void onWindowContentChange(Position pos, ItemStack item);

    /**
     * @param newTitle The new title that the window changed to
     */
    void onWindowTitleChange(String newTitle);
}
