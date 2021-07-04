package io.github.plugindustry.wheelcore.inventory;

import io.github.plugindustry.wheelcore.interfaces.inventory.InventoryClickInfo;
import io.github.plugindustry.wheelcore.interfaces.inventory.WindowInteractor;
import io.github.plugindustry.wheelcore.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ClassicInventoryInteractor implements WindowInteractor, InventoryHolder {
    private final Window window;
    private final Inventory inv;

    public ClassicInventoryInteractor(Window window) {
        this.window = window;
        window.link(this);
        this.inv = Bukkit.createInventory(this, 9 * window.windowSize.height, window.title);
    }

    @Override
    public boolean processClick(InventoryClickInfo info) {
        if (info.slotType == InventoryType.SlotType.OUTSIDE)
            return false;
        return window.onClick(InventoryUtil.convertToPos(info.slot, window.windowSize), info);
    }

    @Override
    public void onWindowChange(Position pos, ItemStack item) {
        inv.setItem(InventoryUtil.convertToSlotNumber(pos, window.windowSize), item);
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        return inv;
    }
}
