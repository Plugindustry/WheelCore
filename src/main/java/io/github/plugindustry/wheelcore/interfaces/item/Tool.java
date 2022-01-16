package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface Tool {
    float getToolBonus(@Nonnull Block block, @Nonnull ItemStack tool);

    boolean isSuitable(@Nonnull Block block, @Nonnull ItemStack tool);
}
