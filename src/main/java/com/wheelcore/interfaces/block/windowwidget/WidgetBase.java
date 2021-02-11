package com.wheelcore.interfaces.block.windowwidget;

import com.wheelcore.interfaces.Base;
import com.wheelcore.inventory.Position;
import com.wheelcore.inventory.SlotSize;
import com.wheelcore.inventory.widget.WidgetType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface WidgetBase extends Base {
    boolean isClickable = false;

    String getId();

    SlotSize getSize();

    WidgetType getWidgetType();
    Map<Position, ItemStack> getChangeMap();
}
