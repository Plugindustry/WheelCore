package io.github.plugindustry.wheelcore.item;

import io.github.plugindustry.wheelcore.interfaces.Interactive;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.inventory.InventoryWindow;
import io.github.plugindustry.wheelcore.inventory.Position;
import io.github.plugindustry.wheelcore.inventory.SlotSize;
import io.github.plugindustry.wheelcore.inventory.WindowInteractor;
import io.github.plugindustry.wheelcore.inventory.widget.WidgetFixedItem;
import io.github.plugindustry.wheelcore.inventory.widget.WidgetProgressBar;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class Tester implements ItemBase, Interactive {
    InventoryWindow window;
    WindowInteractor windowInteractor;

    public Tester() {
        window = new InventoryWindow(new SlotSize(9, 6), "Test");
        window.addWidget(new WidgetProgressBar("process_1"), new Position(2, 3));
        window.addWidget(new WidgetFixedItem("f_1", new ItemStack(Material.IRON_INGOT, 1)), new Position(3, 5));
        windowInteractor = new WindowInteractor(window);
    }

    public boolean onInteract(Player player, Action action, ItemStack tool, Block block, InteractActor actor) {
        if (actor == InteractActor.ITEM && block != null && action == Action.RIGHT_CLICK_BLOCK) {
            player.openInventory(windowInteractor.getInventory());
            ((WidgetProgressBar) window.getWidgetMap().get("process_1").getValue()).setProgress(0.6);
        }

        return false;
    }
}
