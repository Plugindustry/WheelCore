package io.github.plugindustry.wheelcore.inventory;

import io.github.plugindustry.wheelcore.interfaces.Tickable;
import io.github.plugindustry.wheelcore.interfaces.block.windowwidget.WidgetBase;
import io.github.plugindustry.wheelcore.interfaces.block.windowwidget.WidgetClickable;
import io.github.plugindustry.wheelcore.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.Map;

public class WindowInteractor implements InventoryInteractor, InventoryHolder, Tickable {
    private InventoryWindow window;
    private Inventory inv;

    public WindowInteractor(InventoryWindow window) {
        this.window = window;
        this.inv = Bukkit.createInventory(this, 9 * window.windowSize.height, window.title);
    }

    public void renderInventory() {
        Map<String, AbstractMap.SimpleEntry<Position, WidgetBase>> widgetMap = window.getWidgetMap();
        Map<Position, ItemStack> changeMap;
        int slotId;

        for (AbstractMap.SimpleEntry<Position, WidgetBase> currentWidget : widgetMap.values()) {
            changeMap = currentWidget.getValue().getChangeMap();
            for (Map.Entry<Position, ItemStack> entry : changeMap.entrySet()) {
                slotId = InventoryUtil.convertToSlotNumber(InventoryUtil.getAbsolutePos(entry.getKey(), currentWidget.getKey()), window.windowSize);
                inv.setItem(slotId, entry.getValue());
            }
        }
    }

    public boolean processClick(int slot, InventoryClickEvent event) {
        Position slotPos = InventoryUtil.convertToPos(slot, window.windowSize);
        AbstractMap.SimpleEntry<Position, WidgetBase> widgetEntry = window.getWidgetEntryFromWindowPos(slotPos);
        WidgetBase widget = widgetEntry.getValue();
        if (widget instanceof WidgetClickable) {
            boolean toReturn = ((WidgetClickable) widget).processClick(InventoryUtil.getRelativePos(slotPos, widgetEntry.getKey()), event);
            renderInventory();
            return toReturn;
        } else {
            return true; // true for cancelling the event
        }
    }

    @Override
    public Inventory getInventory() {
        this.renderInventory();
        return inv;
    }

    @Override
    public void onTick() {
        this.renderInventory();
    }
}
