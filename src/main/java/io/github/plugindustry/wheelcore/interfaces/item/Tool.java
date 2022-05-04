package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface Tool extends ItemBase {
    /**
     * @param block The block being damaged
     * @param tool  The tool used to damage the block
     * @return The tool bonus, higher value means faster digging
     */
    float getToolBonus(@Nonnull Block block, @Nonnull ItemStack tool);

    /**
     * @param block The block being damaged
     * @param tool  The tool used to damage the block
     * @return Whether the tool is suitable for the block (i.e. whether the tool bonus should be applied and whether the digging should be slowed down)
     */
    boolean isSuitable(@Nonnull Block block, @Nonnull ItemStack tool);

    /**
     * @param block The block being destroyed
     * @param tool  The tool used to destroy the block
     * @return The items the block should drop, or empty to follow the vanilla behavior
     */
    @Nonnull
    Optional<List<ItemStack>> getOverrideItemDrop(@Nonnull Block block, @Nonnull ItemStack tool);

    /**
     * @param block The block being destroyed
     * @param tool  The tool used to destroy the block
     * @return The amount of experience the block should drop, or empty to follow the vanilla behavior
     */
    @Nonnull
    Optional<Integer> getOverrideExpDrop(@Nonnull Block block, @Nonnull ItemStack tool);
}