package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface Tool {
    float getToolBonus(@Nonnull Block block, @Nonnull ItemStack tool);

    boolean isSuitable(@Nonnull Block block, @Nonnull ItemStack tool);

    @Nonnull
    Optional<List<ItemStack>> getOverrideItemDrop(@Nonnull Block block, @Nonnull ItemStack tool);

    @Nonnull
    Optional<Integer> getOverrideExpDrop(@Nonnull Block block, @Nonnull ItemStack tool);
}
