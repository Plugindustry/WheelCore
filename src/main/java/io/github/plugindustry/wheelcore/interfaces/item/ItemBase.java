package io.github.plugindustry.wheelcore.interfaces.item;

import io.github.plugindustry.wheelcore.interfaces.Base;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public interface ItemBase extends Base {
    @Nonnull
    Material getMaterial();
}
