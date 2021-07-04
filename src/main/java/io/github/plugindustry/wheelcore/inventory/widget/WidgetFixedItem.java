package io.github.plugindustry.wheelcore.inventory.widget;

import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetBase;
import io.github.plugindustry.wheelcore.inventory.Position;
import io.github.plugindustry.wheelcore.inventory.SlotSize;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WidgetFixedItem implements WidgetBase {
    private final SlotSize size = new SlotSize(1, 1);
    private final String id;
    private ItemStack slotItemStack;

    public WidgetFixedItem(String id, ItemStack slotItemStack) {
        this.id = id;
        this.slotItemStack = slotItemStack;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public SlotSize getSize() {
        return size;
    }

    @Override
    public @Nonnull
    Map<Position, ItemStack> getChangeMap(boolean force) {
        if (force) {
            Map<Position, ItemStack> retMap = new HashMap<>();
            retMap.put(new Position(1, 1), slotItemStack);
            return retMap;
        } else
            return Collections.emptyMap();
    }

    public ItemStack getItem() {
        return slotItemStack;
    }

    public void setItem(ItemStack slotItemStack) {
        this.slotItemStack = slotItemStack;
    }
}
