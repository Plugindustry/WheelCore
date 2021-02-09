package com.industrialworld.inventory;

import com.industrialworld.interfaces.block.windowwidget.WidgetBase;
import com.industrialworld.inventory.widget.WidgetButton;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class InventoryWindow {
    public final SlotSize windowSize;
    public String title;
    private String[][] pos2Id;
    private Map<String, AbstractMap.SimpleEntry<Position, WidgetBase>> widgetMap = new HashMap<>();

    public InventoryWindow(SlotSize size, String title) {
        this.title = title;
        this.windowSize = size;
        pos2Id = new String[size.width][size.height];
    }

    public void addWidget(WidgetBase widget, Position pos) {
        String widgetId = widget.getId();
        int width = widget.getSize().width;
        int height = widget.getSize().height;
        int posX = pos.xCoord, posY = pos.yCoord;
        widgetMap.put(widgetId, new AbstractMap.SimpleEntry<Position, WidgetBase>(pos, widget));
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j<= height; j++) {
                pos2Id[i + posX - 1][j + posY - 1] = widgetId;
            }
        }
    }

    public Map<String, AbstractMap.SimpleEntry<Position, WidgetBase>> getWidgetMap() {
        return widgetMap;
    }
}
