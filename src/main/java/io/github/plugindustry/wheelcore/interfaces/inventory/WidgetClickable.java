package io.github.plugindustry.wheelcore.interfaces.inventory;

import io.github.plugindustry.wheelcore.inventory.Position;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface WidgetClickable extends WidgetBase {
    boolean isClickable = true;

    boolean processClick(Position pos, InventoryClickEvent event);
}
