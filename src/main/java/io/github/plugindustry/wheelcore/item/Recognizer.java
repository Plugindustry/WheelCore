package io.github.plugindustry.wheelcore.item;

import io.github.plugindustry.wheelcore.interfaces.item.DummyItem;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.PlayerUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Recognizer extends DummyItem {
    @Override
    public boolean onInteract(@Nonnull Player player, @Nonnull Action action, @Nullable EquipmentSlot hand,
                              @Nullable ItemStack tool, @Nullable Block block, @Nullable Entity entity) {
        if (block != null && action == Action.RIGHT_CLICK_BLOCK) {
            StringBuilder builder = new StringBuilder();
            builder.append(block.getType().name());
            if (MainManager.hasBlock(block.getLocation())) {
                builder.append(' ');
                builder.append(
                        MainManager.getIdFromInstance(MainManager.getBlockInstance(block.getLocation())));
                builder.append(' ');
                builder.append(MainManager.getBlockData(block.getLocation()));
            }
            PlayerUtil.sendActionBar(player, builder.toString());
        }

        return false;
    }
}
