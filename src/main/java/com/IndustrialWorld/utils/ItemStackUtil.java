package com.IndustrialWorld.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemStackUtil {
    public static ItemStackFactory create(Material mtrl) {
        return new ItemStackFactory(mtrl);
    }

    public static boolean isSimilar(ItemStack a, ItemStack b) {
    	if (!a.isSimilar(b)) {
    		return false;
	    }

    	// check NBT tag
	    NBTUtil.NBTValue aNbtIsIWI = NBTUtil.getTagValue(a, "isIWItem");
	    NBTUtil.NBTValue bNbtIsIWI = NBTUtil.getTagValue(b, "isIWItem");
    	if (aNbtIsIWI == null) {
    		return bNbtIsIWI == null;
	    }
    	if (bNbtIsIWI == null) {
    		return false;
	    }
    	if (aNbtIsIWI.asBoolean() == bNbtIsIWI.asBoolean()) {
		    NBTUtil.NBTValue aId = NBTUtil.getTagValue(a, "IWItemId");
		    NBTUtil.NBTValue bId = NBTUtil.getTagValue(b, "IWItemId");

		    if (aId == null) {
		    	return bId == null;
		    }
		    if (bId == null) {
		    	return false;
		    }
		    return Objects.equals(aId.asString(), bId.asString());
	    }
    	return false;
    }


    public static class ItemStackFactory {
        private Material mtrl;
        private int amount = 1;
        private String id, displayName = "";
        private List<String> lore = Arrays.asList("");

        public ItemStackFactory(Material mtrl) { this.mtrl = mtrl; }
        public ItemStackFactory setAmount(int amount) { this.amount = amount; return this; }
        public ItemStackFactory setId(String id) { this.id = id; return this; }
        public ItemStackFactory setLore(List<String> lore) { this.lore = lore; return this; }
        public ItemStackFactory setDisplayName(String dpName) { this.displayName = dpName; return this; }

        public ItemStack getItemStack() {
            ItemStack tmp = new ItemStack(mtrl, amount);
            tmp = NBTUtil.setTagValue(tmp, "isIWItem", new NBTUtil.NBTValue().set(true));
            tmp = NBTUtil.setTagValue(tmp, "IWItemId", new NBTUtil.NBTValue().set(id));
            ItemMeta meta = tmp.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            tmp.setItemMeta(meta);
            return tmp.clone();
        }
    }
}
