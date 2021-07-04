package io.github.plugindustry.wheelcore.item;

import io.github.plugindustry.wheelcore.interfaces.Interactive;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.inventory.ClassicInventoryInteractor;
import io.github.plugindustry.wheelcore.inventory.Position;
import io.github.plugindustry.wheelcore.inventory.SlotSize;
import io.github.plugindustry.wheelcore.inventory.Window;
import io.github.plugindustry.wheelcore.inventory.widget.WidgetFixedItem;
import io.github.plugindustry.wheelcore.inventory.widget.WidgetProgressBar;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class Tester implements ItemBase, Interactive {
    final Window window;
    final ClassicInventoryInteractor windowInteractor;

    public Tester() {
        window = new Window(new SlotSize(9, 6), "Test");
        window.addWidget(new WidgetProgressBar("process_1"), new Position(2, 3));
        window.addWidget(new WidgetFixedItem("f_1", new ItemStack(Material.IRON_INGOT, 1)), new Position(3, 5));
        windowInteractor = new ClassicInventoryInteractor(window);
    }

    @Override
    public boolean onInteract(@Nonnull Player player, @Nonnull Action action, ItemStack tool, Block block) {
        if (block != null && action == Action.RIGHT_CLICK_BLOCK) {
            player.openInventory(windowInteractor.getInventory());
            ((WidgetProgressBar) window.getWidgetMap().get("process_1").getValue()).setProgress(0.6);
        }

        return false;
    }
}
