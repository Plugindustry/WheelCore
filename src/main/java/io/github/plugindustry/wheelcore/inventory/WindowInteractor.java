package io.github.plugindustry.wheelcore.inventory;

import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetBase;
import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetClickable;
import io.github.plugindustry.wheelcore.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;
import java.util.AbstractMap;

public class WindowInteractor implements InventoryInteractor, InventoryHolder {
    private final InventoryWindow window;
    private final Inventory inv;

    public WindowInteractor(InventoryWindow window) {
        this.window = window;
        this.inv = Bukkit.createInventory(this, 9 * window.windowSize.height, window.title);
    }

    @Override
    public void renderInventory() {
        window.getWidgetMap().values().forEach(we -> we.getValue()
                .getChangeMap()
                .forEach((pos, item) -> inv.setItem(InventoryUtil.convertToSlotNumber(InventoryUtil.getAbsolutePos(we.getKey(),
                                                                                                                   pos),
                                                                                      window.windowSize), item)));
    }

    @Override
    public boolean processClick(int slot, InventoryClickEvent event) {
        Position slotPos = InventoryUtil.convertToPos(slot, window.windowSize);
        AbstractMap.SimpleEntry<Position, WidgetBase> widgetEntry = window.getWidgetAt(slotPos);
        if (widgetEntry == null)
            return true;
        WidgetBase widget = widgetEntry.getValue();
        if (widget instanceof WidgetClickable) {
            boolean ret = ((WidgetClickable) widget).processClick(InventoryUtil.getRelativePos(widgetEntry.getKey(),
                                                                                               slotPos), event);
            renderInventory();
            return ret;
        } else {
            return true; // true for cancelling the event
        }
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        this.renderInventory();
        return inv;
    }
}
