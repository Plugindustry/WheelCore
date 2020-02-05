package com.IndustrialWorld.manager;

import com.IndustrialWorld.i18n.I18nConst;
import com.IndustrialWorld.utils.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemManager {
	public static Map<String, ItemStack> itemMap = new HashMap<>();

	public static void register(String id, ItemStack itemStack) {
		itemMap.put(id, itemStack);
	}

	public static ItemStack get(String id) {
		return itemMap.get(id);
	}

	public static String getId(ItemStack itemStack) {
		for (Map.Entry<String, ItemStack> entry : itemMap.entrySet()) {
			if (entry.getValue().equals(itemStack)) {
				return entry.getKey();
			}
		}
		return "";
	}
}
