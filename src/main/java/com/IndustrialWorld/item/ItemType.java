package com.IndustrialWorld.item;

public enum ItemType {
    INGOT("INGOT"),
    DUST("DUST");

    private final String typeID;

    ItemType(String typeID) {
        this.typeID = typeID;
    }

    public String getTypeID() {
        return typeID;
    }
}
