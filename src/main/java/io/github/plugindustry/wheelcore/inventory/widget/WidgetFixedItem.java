package io.github.plugindustry.wheelcore.inventory.widget;

import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetBase;
import io.github.plugindustry.wheelcore.inventory.Position;
import io.github.plugindustry.wheelcore.inventory.SlotSize;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WidgetFixedItem implements WidgetBase {
    private final SlotSize size = new SlotSize(1, 1);
    private final String id;
    private ItemStack slotItemStack;
    private boolean init = false;

    public WidgetFixedItem(String id, ItemStack slotItemStack) {
        this.id = id;
        this.slotItemStack = slotItemStack;
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
    public Map<Position, ItemStack> getChangeMap(boolean force) {
        if (init && !force)
            return Collections.emptyMap();
        else {
            init = true;
            Map<Position, ItemStack> retMap = new HashMap<>();
            retMap.put(new Position(1, 1), slotItemStack);
            return retMap;
        }
    }

    public ItemStack getItem() {
        return slotItemStack;
    }

    public void setItem(ItemStack slotItemStack) {
        this.slotItemStack = slotItemStack;
    }
}
