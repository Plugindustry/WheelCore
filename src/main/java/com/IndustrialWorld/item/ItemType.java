package com.IndustrialWorld.item;

public enum ItemType {
    NULL("NULL"),
    INGOT("INGOT"),
    DUST("DUST");

    private final String typeID;

    ItemType(String typeID) {
        this.typeID = typeID;
    }

    public String getTypeID() { return typeID; }
}
