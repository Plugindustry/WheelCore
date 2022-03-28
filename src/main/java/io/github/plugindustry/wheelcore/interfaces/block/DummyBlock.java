package io.github.plugindustry.wheelcore.interfaces.block;

import io.github.plugindustry.wheelcore.interfaces.Interactive;
import io.github.plugindustry.wheelcore.manager.ItemMapping;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.BlockUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class DummyBlock implements BlockBase, Placeable, Destroyable, Interactive {
    @Override
    public boolean onInteract(@Nonnull Player player, @Nonnull Action action, ItemStack tool, Block block, Entity entity) {
        return true;
    }

    @Override
    public boolean onBlockPlace(@Nullable ItemStack item, @Nonnull Block block, @Nullable Block blockAgainst, @Nullable Player player) {
        // We do nothing by default, so you should do this job in your implementation too.
        MainManager.addBlock(block.getLocation(), this, getInitialData(item, block, blockAgainst, player));
        if (item == null || getMaterial() != item.getType())
            block.setType(getMaterial());
        return true;
    }

    @Override
    public boolean onBlockDestroy(@Nonnull Block block, @Nonnull DestroyMethod method, ItemStack tool, @Nullable Player player) {
        // We do nothing by default, so you should do this job in your implementation too.
        if (method == DestroyMethod.PHYSICS || method == DestroyMethod.FADE)
            return false;

        MainManager.removeBlock(block.getLocation());
        block.setType(Material.AIR);
        if (player == null || player.getGameMode() != GameMode.CREATIVE)
            block.getWorld().dropItem(block.getLocation(), getItemStack());
        return true;
    }

    @Override
    public float getHardness(@Nonnull Block block) {
        return BlockUtil.getVanillaHardness(block);
    }

    @Override
    public boolean isPreferredTool(@Nonnull Block block, @Nonnull ItemStack tool) {
        return BlockUtil.isVanillaPreferredTool(block, tool);
    }

    @Override
    public boolean needCorrectTool(@Nonnull Block block) {
        return BlockUtil.vanillaNeedCorrectTool(block);
    }

    @Nonnull
    public ItemStack getItemStack() {
        return ItemMapping.get(Objects.requireNonNull(MainManager.getIdFromInstance(this)));
    }

    @Override
    @Nonnull
    public Material getMaterial() {
        return getItemStack().getType();
    }

    @Nullable
    public BlockData getInitialData(@Nullable ItemStack item, @Nonnull Block block, @Nullable Block blockAgainst, @Nullable Player player) {
        return null;
    }
}
