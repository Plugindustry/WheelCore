package com.IndustrialWorld.item.material;

public enum IWMaterial {
    COPPER("COPPER"),
    IRON("IRON");

    private final String materialID;

    IWMaterial(String materialID) {
        this.materialID = materialID;
    }

    public String getMaterialID() {
        return materialID;
    }
}
