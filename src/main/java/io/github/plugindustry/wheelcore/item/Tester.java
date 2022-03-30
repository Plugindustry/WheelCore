package io.github.plugindustry.wheelcore.item;

import io.github.plugindustry.wheelcore.interfaces.inventory.Position;
import io.github.plugindustry.wheelcore.interfaces.inventory.SlotSize;
import io.github.plugindustry.wheelcore.interfaces.item.DummyItem;
import io.github.plugindustry.wheelcore.inventory.ClassicInventoryInteractor;
import io.github.plugindustry.wheelcore.inventory.Window;
import io.github.plugindustry.wheelcore.inventory.widget.WidgetFixedItem;
import io.github.plugindustry.wheelcore.inventory.widget.WidgetProgressBar;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Tester extends DummyItem {
    final Window window;
    final ClassicInventoryInteractor windowInteractor;

    public Tester() {
        window = new Window(new SlotSize(9, 6), "Test");
        window.addWidget(new WidgetProgressBar("process_1"), new Position(2, 3));
        window.addWidget(new WidgetFixedItem("f_1", new ItemStack(Material.IRON_INGOT, 1)), new Position(3, 5));
        windowInteractor = new ClassicInventoryInteractor(window);
    }

    @Override
    public boolean onInteract(@Nonnull Player player, @Nonnull Action action, @Nullable EquipmentSlot hand, @Nullable ItemStack tool, @Nullable Block block, @Nullable Entity entity) {
        if (block != null && action == Action.RIGHT_CLICK_BLOCK) {
            player.openInventory(windowInteractor.getInventory());
            ((WidgetProgressBar) window.getWidgetMap().get("process_1").second).setProgress(0.6);
        }

        return false;
    }
}
