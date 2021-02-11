package io.github.plugindustry.wheelcore.utils;

import org.bukkit.Material;

public class CompatMaterial {
    public static final Material IRON_SHOVEL = (Material.getMaterial("IRON_SHOVEL") == null) ? Material.getMaterial(
            "IRON_SPADE") : Material.getMaterial("IRON_SHOVEL");
    //TODO: Multi-version Material

}
