package com.IndustrialWorld.manager;

import com.IndustrialWorld.item.ItemType;
import com.IndustrialWorld.item.material.IWMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemManager {
	public static Map<String, ItemStack> itemMap = new HashMap<>();
	public static Map<ItemType, Map<IWMaterial, Material>> itemMaterialMap = new HashMap<>();

	public static void register(String id, ItemStack itemStack) {
		itemMap.put(id, itemStack);
	}

	public static void registerItemMaterial(ItemType type, IWMaterial iwMaterial, Material bukkitMaterial) {
		itemMaterialMap.put(type, (Map<IWMaterial, Material>) new HashMap<>().put(iwMaterial, bukkitMaterial));
	}

	public static ItemStack get(String id) {
		return itemMap.get(id);
	}

	public static Material getItemMaterial(ItemType type, IWMaterial iwMaterial) {
		return itemMaterialMap.get(type).get(iwMaterial);
	}

	public static String getId(ItemStack itemStack) {
		for (Map.Entry<String, ItemStack> entry : itemMap.entrySet()) {
			if (entry.getValue().equals(itemStack)) {
				return entry.getKey();
			}
		}
		return "";
	}

	public static Boolean isItemExists(String id){
		return itemMap.containsKey(id);
	}
}
