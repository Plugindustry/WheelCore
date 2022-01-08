package io.github.plugindustry.wheelcore.interfaces.inventory;

import io.github.plugindustry.wheelcore.interfaces.Base;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public interface WidgetBase extends Base {
    /**
     * @return The ID of this widget
     */
    @Nonnull
    String getId();

    /**
     * @return The size of this widget
     */
    @Nonnull
    SlotSize getSize();

    /**
     * @param force If we should return the entire content of this widget no matter whether there are new changes
     * @return A map containing all changes needed to change the state of this widget from the previous one to a new one
     */
    @Nonnull
    Map<Position, ItemStack> getChangeMap(boolean force);
}
