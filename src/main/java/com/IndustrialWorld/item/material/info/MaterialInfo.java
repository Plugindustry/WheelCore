package com.IndustrialWorld.item.material.info;

import com.IndustrialWorld.item.material.IWMaterial;


public class MaterialInfo {
    private final IWMaterial iwMaterial;
    private short level;

    public static MaterialInfo newInstance(IWMaterial iwMaterial) {
        return new MaterialInfo(iwMaterial);
    }

    public MaterialInfo(IWMaterial iwMaterial) {
        this.iwMaterial = iwMaterial;
    }

    public MaterialInfo setLevel(short level) {
        this.level = level;
        return this;
    }

    public short getLevel() {
        return level;
    }
}
