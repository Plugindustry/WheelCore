package io.github.plugindustry.wheelcore.interfaces.block;

import io.github.plugindustry.wheelcore.interfaces.Base;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface BlockBase extends Base {
    ItemStack getItemStack();

    Material getMaterial();
}
