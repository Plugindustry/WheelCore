package io.github.plugindustry.wheelcore.manager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;

public class ItemMapping {
    public static final HashMap<String, ItemStack> itemMap = new HashMap<>();
    public static final HashMap<String, HashSet<Material>> dictMaterial = new HashMap<>();

    public static void set(String id, ItemStack itemStack) {
        itemMap.put(id, itemStack);
        MainManager.getItemOreDictionary(itemStack).forEach(dict -> {
            if (!dictMaterial.containsKey(dict))
                dictMaterial.put(dict, new HashSet<>());
            dictMaterial.get(dict).add(itemStack.getType());
        });
    }

    public static ItemStack get(String id) {
        return itemMap.get(id).clone();
    }

    public static boolean isItemExists(String id) {
        return itemMap.containsKey(id);
    }
}
