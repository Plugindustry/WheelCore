package io.github.plugindustry.wheelcore.interfaces.block;

import io.github.plugindustry.wheelcore.ConstItems;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public abstract class MachineBase extends DummyBlock {
    @Override
    public boolean onBlockDestroy(Block block, ItemStack tool, DestroyMethod method) {
        if (method == DestroyMethod.EXPLOSION) {
            MainManager.removeBlock(block.getLocation());
            block.getWorld().dropItem(block.getLocation(), ConstItems.BASIC_MACHINE_BLOCK);
            return true;
        } else
            return super.onBlockDestroy(block, tool, method);
    }
}
