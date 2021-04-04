package io.github.plugindustry.wheelcore.interfaces.item;

import org.bukkit.Material;

public abstract class DummyItem implements ItemBase {
    public Material displayBukkitMaterial;

    private String unlocalizedName;

    private void setUnlocalizedName(String name) {
        this.unlocalizedName = "item." + name;
    }

    private void setDisplayBukkitMaterial(Material material) {
        this.displayBukkitMaterial = material;
    }
}
