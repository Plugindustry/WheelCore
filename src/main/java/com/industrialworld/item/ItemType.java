package com.industrialworld.item;

import com.industrialworld.item.template.ItemIngot;
import com.industrialworld.item.template.ItemTemplate;

public enum ItemType {
    NULL("NULL", ItemTemplate.getInstance()), INGOT("INGOT", ItemIngot.getInstance()), DUST("DUST",
                                                                                            ItemTemplate.getInstance());

    private final String typeID;
    private final ItemTemplate template;

    ItemType(String typeID, ItemTemplate template) {
        this.typeID = typeID;
        this.template = template;
    }

    public String getTypeID() {
        return typeID;
    }

    @Override
    public String toString() {
        return typeID;
    }

    public ItemTemplate getTemplate() {
        return template;
    }
}
