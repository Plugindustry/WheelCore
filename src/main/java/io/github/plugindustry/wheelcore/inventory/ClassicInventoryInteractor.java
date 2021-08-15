package io.github.plugindustry.wheelcore.inventory;

import io.github.plugindustry.wheelcore.interfaces.inventory.InventoryClickInfo;
import io.github.plugindustry.wheelcore.interfaces.inventory.WindowInteractor;
import io.github.plugindustry.wheelcore.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ClassicInventoryInteractor implements WindowInteractor, InventoryHolder {
    private final Window window;
    private Inventory inv;

    public ClassicInventoryInteractor(Window window) {
        this.window = window;
        window.link(this);
    }

    @Override
    public boolean processClick(InventoryClickInfo info) {
        if (info.slotType == InventoryType.SlotType.OUTSIDE)
            return false;
        return window.onClick(InventoryUtil.convertToPos(info.slot, window.windowSize), info);
    }

    @Override
    public void processClose(HumanEntity whoClosed) {
        window.onClose(whoClosed);
    }

    @Override
    public void onWindowContentChange(Position pos, ItemStack item) {
        inv.setItem(InventoryUtil.convertToSlotNumber(pos, window.windowSize), item);
    }

    @Override
    public void onWindowTitleChange(String newTitle) {
        inv = Bukkit.createInventory(this, 9 * window.windowSize.height, newTitle);
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        return inv;
    }
}
