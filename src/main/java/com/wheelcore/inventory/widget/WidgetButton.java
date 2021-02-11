package com.wheelcore.inventory.widget;

import com.wheelcore.interfaces.block.windowwidget.WidgetClickable;
import com.wheelcore.inventory.Position;
import com.wheelcore.inventory.SlotSize;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class WidgetButton implements WidgetClickable {
    private final SlotSize size = new SlotSize(1, 1);
    String id;
    ItemStack slotItemStack;
    Function onClickFunc;
    private boolean init = false;

    public WidgetButton(String id, ItemStack slotItemStack, Function onClickFunc) {
        this.id = id;
        this.slotItemStack = slotItemStack;
        this.onClickFunc = onClickFunc;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public SlotSize getSize() {
        return size;
    }

    @Override
    public WidgetType getWidgetType() {
        return WidgetType.BUTTON;
    }

    @Override
    public Map<Position, ItemStack> getChangeMap() {
        if (init) {
            return new HashMap<>();
        } else {
            init = true;
            Map<Position, ItemStack> retMap = new HashMap<>();
            retMap.put(new Position(1, 1), slotItemStack);
            return retMap;
        }
    }

    @Override
    public boolean processClick(Position pos, InventoryClickEvent event) {
        // TODO process click
        // return true for cancelling the event, false for doing nothing
        return true;
    }
}
