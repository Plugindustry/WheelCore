package io.github.plugindustry.wheelcore.manager;

import io.github.plugindustry.wheelcore.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;

public class ItemMapping {
    public static HashMap<String, ItemStack> itemMap = new HashMap<>();
    public static HashMap<String, HashSet<Material>> dictMaterial = new HashMap<>();

    public static void set(String id, ItemStack itemStack) {
        itemMap.put(id, itemStack);
        ItemStackUtil.getOreDictionary(itemStack).forEach(dict -> {
            if (!dictMaterial.containsKey(dict))
                dictMaterial.put(dict, new HashSet<>());
            dictMaterial.get(dict).add(itemStack.getType());
        });
    }

    public static ItemStack get(String id) {
        return itemMap.get(id);
    }

    public static boolean isItemExists(String id) {
        return itemMap.containsKey(id);
    }
}
