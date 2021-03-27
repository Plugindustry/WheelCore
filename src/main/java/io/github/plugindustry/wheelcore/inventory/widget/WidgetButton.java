package io.github.plugindustry.wheelcore.inventory.widget;

import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetClickable;
import io.github.plugindustry.wheelcore.inventory.Position;
import io.github.plugindustry.wheelcore.inventory.SlotSize;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WidgetButton implements WidgetClickable {
    private final SlotSize size = new SlotSize(1, 1);
    String id;
    ItemStack slotItemStack;
    BiConsumer<Position, InventoryClickEvent> onClickFunc;
    private boolean init = false;

    public WidgetButton(String id, ItemStack slotItemStack, BiConsumer<Position, InventoryClickEvent> onClickFunc) {
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
            return Collections.emptyMap();
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
        onClickFunc.accept(pos, event);
        return true;
    }
}
