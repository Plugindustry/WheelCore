package io.github.plugindustry.wheelcore.interfaces.inventory;

import io.github.plugindustry.wheelcore.interfaces.Base;
import io.github.plugindustry.wheelcore.inventory.Position;
import io.github.plugindustry.wheelcore.inventory.SlotSize;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface WidgetBase extends Base {
    String getId();

    SlotSize getSize();

    Map<Position, ItemStack> getChangeMap(boolean force);
}
