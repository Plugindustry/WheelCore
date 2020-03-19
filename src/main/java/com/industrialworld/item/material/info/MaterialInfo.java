package com.industrialworld.item.material.info;

import com.industrialworld.item.material.IWMaterial;


public class MaterialInfo {
    private final IWMaterial iwMaterial;
    private short level;

    public MaterialInfo(IWMaterial iwMaterial) {
        this.iwMaterial = iwMaterial;
    }

    public static MaterialInfo newInstance(IWMaterial iwMaterial) {
        return new MaterialInfo(iwMaterial);
    }

    public short getLevel() {
        return level;
    }

    public MaterialInfo setLevel(short level) {
        this.level = level;
        return this;
    }
}
