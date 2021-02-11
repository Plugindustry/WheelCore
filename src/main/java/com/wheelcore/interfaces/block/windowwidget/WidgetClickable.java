package com.wheelcore.interfaces.block.windowwidget;

import com.wheelcore.inventory.Position;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface WidgetClickable extends WidgetBase {
    boolean isClickable = true;

    boolean processClick(Position pos, InventoryClickEvent event);
}
