package com.industrialworld.inventory.widget;

import com.industrialworld.interfaces.block.windowwidget.WidgetBase;
import com.industrialworld.interfaces.block.windowwidget.WidgetClickable;
import com.industrialworld.inventory.Position;
import com.industrialworld.inventory.SlotSize;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.graalvm.compiler.lir.stackslotalloc.LSStackSlotAllocator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class WidgetButton implements WidgetClickable {
    private final SlotSize size = new SlotSize(1, 1);
    String id;
    ItemStack slotItemStack;
    Function onClickFunc;
    private boolean init = false;

    public WidgetButton(String id, Function onClickFunc) {
        this.id = id;
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
    public boolean processClick(Position pos, ClickType clickType) {
        // TODO process click
        // return true for cancelling the event, false for doing nothing
        return true;
    }
}
