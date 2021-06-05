package io.github.plugindustry.wheelcore.inventory;

import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetBase;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class InventoryWindow {
    public final SlotSize windowSize;
    private final String[][] pos2Id;
    private final Map<String, AbstractMap.SimpleEntry<Position, WidgetBase>> widgetMap = new HashMap<>();
    public String title;

    public InventoryWindow(SlotSize size, String title) {
        this.title = title;
        this.windowSize = size;
        pos2Id = new String[size.width + 1][size.height + 1];
    }

    public void addWidget(WidgetBase widget, Position pos) {
        String widgetId = widget.getId();
        int width = widget.getSize().width;
        int height = widget.getSize().height;
        int posX = pos.xCoord, posY = pos.yCoord;
        widgetMap.put(widgetId, new AbstractMap.SimpleEntry<>(pos, widget));
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                pos2Id[i + posX - 1][j + posY - 1] = widgetId;
            }
        }
    }

    public Map<String, AbstractMap.SimpleEntry<Position, WidgetBase>> getWidgetMap() {
        return widgetMap;
    }

    public String[][] getPos2Id() {
        return pos2Id;
    }

    public AbstractMap.SimpleEntry<Position, WidgetBase> getWidgetAt(Position pos) {
        return widgetMap.get(pos2Id[pos.xCoord][pos.yCoord]);
    }
}
