package com.IndustrialWorld.item;

import com.IndustrialWorld.item.template.ItemIngot;
import com.IndustrialWorld.item.template.ItemTemplate;

public enum ItemType {
    NULL("NULL", ItemTemplate.class),
    INGOT("INGOT", ItemIngot.class),
    DUST("DUST", ItemTemplate.class);

    private final String typeID;
    private final Class<ItemTemplate> templateClass;

    ItemType(String typeID, Class<? extends ItemTemplate> templateClass) {
        this.typeID = typeID;
        this.templateClass = (Class<ItemTemplate>) templateClass;
    }

    public String getTypeID() { return typeID; }

    @Override
    public String toString() {
        return typeID;
    }

    public Class<ItemTemplate> getTemplateClass() {
        return templateClass;
    }
}
