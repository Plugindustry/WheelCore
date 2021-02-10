package com.industrialworld.item;

import com.industrialworld.interfaces.Interactive;
import com.industrialworld.interfaces.item.ItemBase;
import com.industrialworld.inventory.InventoryWindow;
import com.industrialworld.inventory.Position;
import com.industrialworld.inventory.SlotSize;
import com.industrialworld.inventory.WindowInteractor;
import com.industrialworld.inventory.widget.WidgetProgressBar;
import com.industrialworld.manager.MainManager;
import com.industrialworld.utils.PlayerUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Tester implements ItemBase, Interactive {
    InventoryWindow window;
    WindowInteractor windowInteractor;

    public Tester() {
        window = new InventoryWindow(new SlotSize(9, 6), "Test");
        window.addWidget(new WidgetProgressBar("process_1"), new Position(2, 3));
        windowInteractor = new WindowInteractor(window);
    }

    public boolean onInteract(Player player, Action action, ItemStack tool, Block block, InteractActor actor) {
        if (actor == InteractActor.ITEM && block != null && action == Action.RIGHT_CLICK_BLOCK) {
            player.openInventory(windowInteractor.getInventory());
        }

        return false;
    }
}
