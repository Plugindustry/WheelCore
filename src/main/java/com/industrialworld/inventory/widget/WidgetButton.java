package com.industrialworld.inventory.widget;

import com.industrialworld.interfaces.block.windowwidget.WidgetBase;
import com.industrialworld.inventory.Position;
import com.industrialworld.inventory.SlotSize;
import org.bukkit.inventory.ItemStack;
import org.graalvm.compiler.lir.stackslotalloc.LSStackSlotAllocator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class WidgetButton implements WidgetBase {
    private final SlotSize size = new SlotSize(1, 1);
    String id;
    ItemStack slotItemStack;
    Function onClickFunc;

    public WidgetButton(String id, Function onClickFunc) {
        this.id = id;
        this.onClickFunc = onClickFunc;
    }

    public boolean onClick() {
        onClickFunc.apply(null);
        return false;
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
        return new HashMap<>();
    }
}
