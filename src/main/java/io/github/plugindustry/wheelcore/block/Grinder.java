package io.github.plugindustry.wheelcore.block;

import io.github.plugindustry.wheelcore.interfaces.block.MachineBase;
import io.github.plugindustry.wheelcore.manager.ItemMapping;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Grinder extends MachineBase {
    @Override
    public ItemStack getItemStack() {
        return ItemMapping.get("GRINDER");
    }

    @Override
    public Material getMaterial() {
        return ItemMapping.get("GRINDER").getType();
    }
}
