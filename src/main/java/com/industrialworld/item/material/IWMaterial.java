package com.industrialworld.item.material;

public enum IWMaterial {
    NULL("NULL"), COPPER("COPPER"), IRON("IRON"), STEEL("STEEL");

    private final String materialID;

    IWMaterial(String materialID) {
        this.materialID = materialID;
    }

    public String getMaterialID() {
        return materialID;
    }
}
