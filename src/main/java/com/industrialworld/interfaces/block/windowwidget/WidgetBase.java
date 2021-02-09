package com.industrialworld.interfaces.block.windowwidget;

import com.industrialworld.interfaces.Base;
import com.industrialworld.inventory.Position;
import com.industrialworld.inventory.SlotSize;
import com.industrialworld.inventory.widget.WidgetType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface WidgetBase extends Base {
    boolean isClickable = false;

    String getId();

    SlotSize getSize();

    WidgetType getWidgetType();
    Map<Position, ItemStack> getChangeMap();
}
