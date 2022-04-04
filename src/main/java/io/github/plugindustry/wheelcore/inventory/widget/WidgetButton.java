package io.github.plugindustry.wheelcore.inventory.widget;

import io.github.plugindustry.wheelcore.interfaces.inventory.InventoryClickInfo;
import io.github.plugindustry.wheelcore.interfaces.inventory.Position;
import io.github.plugindustry.wheelcore.interfaces.inventory.SlotSize;
import io.github.plugindustry.wheelcore.interfaces.inventory.WidgetClickable;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WidgetButton implements WidgetClickable {
    private final SlotSize size = new SlotSize(1, 1);
    private final String id;
    private final BiConsumer<Position, InventoryClickInfo> onClickFunc;
    private ItemStack slotItemStack;
    private boolean changed = true;

    public WidgetButton(String id, ItemStack slotItemStack, BiConsumer<Position, InventoryClickInfo> onClickFunc) {
        this.id = id;
        this.slotItemStack = slotItemStack;
        this.onClickFunc = onClickFunc;
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
        if (changed || force) {
            Map<Position, ItemStack> retMap = new HashMap<>();
            retMap.put(new Position(1, 1), slotItemStack);
            changed = false;
            return retMap;
        } else return Collections.emptyMap();
    }

    @Override
    public boolean onClick(Position pos, InventoryClickInfo info) {
        // return true for cancelling the event, false for doing nothing
        onClickFunc.accept(pos, info);
        return true;
    }

    @Override
    public boolean isClickable() {
        return true;
    }

    public ItemStack getItem() {
        return slotItemStack;
    }

    public void setItem(ItemStack slotItemStack) {
        this.slotItemStack = slotItemStack;
        this.changed = true;
    }
}