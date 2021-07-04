package io.github.plugindustry.wheelcore.interfaces.inventory;

import io.github.plugindustry.wheelcore.inventory.Position;

public interface WidgetClickable extends WidgetBase {
    boolean onClick(Position pos, InventoryClickInfo info);

    boolean isClickable();
}
