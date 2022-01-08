package io.github.plugindustry.wheelcore.interfaces.inventory;

public interface WidgetClickable extends WidgetBase {
    /**
     * @param pos  The relative pos where the click was performed
     * @param info The information about the click performed
     * @return Whether the click should be cancelled
     */
    boolean onClick(Position pos, InventoryClickInfo info);

    /**
     * @return Whether this widget is clickable
     */
    boolean isClickable();
}
