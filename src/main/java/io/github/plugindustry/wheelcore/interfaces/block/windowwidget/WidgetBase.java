package io.github.plugindustry.wheelcore.interfaces.block.windowwidget;

import io.github.plugindustry.wheelcore.interfaces.Base;
import io.github.plugindustry.wheelcore.inventory.Position;
import io.github.plugindustry.wheelcore.inventory.SlotSize;
import io.github.plugindustry.wheelcore.inventory.widget.WidgetType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface WidgetBase extends Base {
    boolean isClickable = false;

    String getId();

    SlotSize getSize();

    WidgetType getWidgetType();
    Map<Position, ItemStack> getChangeMap();
}
