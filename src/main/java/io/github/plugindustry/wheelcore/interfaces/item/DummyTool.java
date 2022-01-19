package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public abstract class DummyTool extends DummyItem implements Tool {
    @Nonnull
    @Override
    public Optional<List<ItemStack>> getOverrideItemDrop(@Nonnull Block block, @Nonnull ItemStack tool) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<Integer> getOverrideExpDrop(@Nonnull Block block, @Nonnull ItemStack tool) {
        return Optional.empty();
    }
}
