package com.industrialworld.i18n;

public class I18nConst {
    public static class ItemType {
        public static String INGOT = I18n.getLocaleString("itemtype.ingot.name");
        public static String INGOT_LORE = I18n.getLocaleString("itemtype.ingot.lore");
    }

    public static class Item {
        public static final String ITEM_NAME = "item.%s.name";
        public static final String ITEM_LORE = "item.%s.lore";
    }

    public static class Block {
        // TODO I18NCONST Blocks
    }

    public static class Enchantment {
        public static final String ENCHANTMENT_NAME = "enchantment.%s.name";
        public static final String ENCHANTMENT_LEVEL = "enchantment.level.%d";
    }

    public static class Element {
        public static final String Cu = "element.cu";
        public static final String Fe = "element.fe";
        public static final String Pb = "element.pb";
        public static final String Zn = "element.zn";
        public static final String Sn = "element.sn";
    }

    public static class Formula {
        // TODO I18NCONST Formulas
    }

    public static class Inventory {
        // TODO I18NCONST Inventories
    }
}
