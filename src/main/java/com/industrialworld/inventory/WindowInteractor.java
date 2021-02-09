package com.industrialworld.inventory;

import com.industrialworld.i18n.I18nConst;
import com.industrialworld.interfaces.block.windowwidget.WidgetBase;
import com.industrialworld.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.Map;

public class WindowInteractor implements InventoryInteractor, InventoryHolder {
    private InventoryWindow window;
    private Inventory inv;

    public WindowInteractor(InventoryWindow window) {
        this.window = window;
        this.inv = Bukkit.createInventory(null, 9 * window.windowSize.height, window.title);
    }

    public void renderInventory() {
        Map<String, AbstractMap.SimpleEntry<Position, WidgetBase>> widgetMap = window.getWidgetMap();
        Map<Position, ItemStack> changeMap;
        Position currentPos;
        int slotId;

        for (AbstractMap.SimpleEntry<Position, WidgetBase> currentWidget : widgetMap.values()) {
            changeMap = currentWidget.getValue().getChangeMap();
            for (Map.Entry<Position, ItemStack> entry : changeMap.entrySet()) {
                currentPos = new Position(entry.getKey().xCoord + currentWidget.getKey().xCoord - 1, entry.getKey().yCoord + currentWidget.getKey().yCoord - 1);
                slotId = InventoryUtil.convertToSlotNumber(currentWidget.getKey(), window.windowSize);
                inv.setItem(slotId, entry.getValue());
            }
        }
    }

    @Override
    public Inventory getInventory() {
        this.renderInventory();
        return inv;
    }
}
