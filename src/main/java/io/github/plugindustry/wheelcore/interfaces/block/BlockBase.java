package io.github.plugindustry.wheelcore.interfaces.block;

import io.github.plugindustry.wheelcore.interfaces.Base;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public interface BlockBase extends Base {
    @Nonnull
    Material getMaterial();
}
