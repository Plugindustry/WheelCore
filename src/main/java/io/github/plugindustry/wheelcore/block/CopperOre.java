package io.github.plugindustry.wheelcore.block;

import io.github.plugindustry.wheelcore.ConstItems;
import io.github.plugindustry.wheelcore.interfaces.block.DummyBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CopperOre extends DummyBlock {
    @Override
    public ItemStack getItemStack() {
        return ConstItems.COPPER_ORE;
    }

    @Override
    public Material getMaterial() {
        return ConstItems.COPPER_ORE.getType();
    }

}
