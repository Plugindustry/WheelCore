package com.IndustrialWorld.manager;

import com.IndustrialWorld.item.ItemType;
import com.IndustrialWorld.item.material.IWMaterial;
import com.IndustrialWorld.item.template.ItemIngot;
import com.IndustrialWorld.utils.DebuggingLogger;
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
		Map<IWMaterial, Material> materialMap;

		if (itemMaterialMap.containsKey(type)) {
			materialMap = itemMaterialMap.get(type);
			materialMap.put(iwMaterial, bukkitMaterial);
		} else {
			materialMap = new HashMap<>();
			materialMap.put(iwMaterial, bukkitMaterial);
			itemMaterialMap.put(type, materialMap);
		}
	}

	public static ItemStack get(String id) {
		return itemMap.get(id);
	}

	public static Material getItemMaterial(ItemType type, IWMaterial iwMaterial) {
		if (itemMaterialMap.containsKey(type)) {
			if (itemMaterialMap.get(type).containsKey(iwMaterial)) {
				return itemMaterialMap.get(type).get(iwMaterial);
			} else {
				DebuggingLogger.debug("No BukkitMaterial for IWMaterial:" + iwMaterial.getMaterialID() + " & ItemType:" + type.getTypeID() + "was found.");
			}
		} else {
			DebuggingLogger.debug("No Itemtype was found. " + type.getTypeID());
		}

		return Material.AIR;
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
