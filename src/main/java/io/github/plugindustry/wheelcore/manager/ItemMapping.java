package io.github.plugindustry.wheelcore.manager;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ItemMapping {
    public static final HashMap<NamespacedKey, ItemStack> itemMap = new HashMap<>();
    public static final HashMap<String, HashSet<Material>> dictMaterial = new HashMap<>();
    public static final HashMap<Material, Set<String>> vanillaOreDict = new HashMap<>();

    public static void set(@Nonnull NamespacedKey id, @Nonnull ItemStack itemStack) {
        itemMap.put(id, itemStack);
        MainManager.getItemOreDictionary(itemStack).forEach(dict -> {
            if (!dictMaterial.containsKey(dict)) dictMaterial.put(dict, new HashSet<>());
            dictMaterial.get(dict).add(itemStack.getType());
        });
    }

    public static ItemStack get(@Nonnull NamespacedKey id) {
        return itemMap.get(id).clone();
    }

    public static boolean isItemExists(@Nonnull NamespacedKey id) {
        return itemMap.containsKey(id);
    }

    public static void registerVanillaOreDict(@Nonnull Material type, @Nonnull Set<String> dict) {
        dict.forEach(dictName -> {
            if (!dictMaterial.containsKey(dictName)) dictMaterial.put(dictName, new HashSet<>());
            dictMaterial.get(dictName).add(type);
        });
        if (vanillaOreDict.containsKey(type)) vanillaOreDict.get(type).addAll(dict);
        else vanillaOreDict.put(type, new HashSet<>(dict));
    }

    @Nonnull
    public static Set<String> getVanillaOreDict(@Nonnull Material type) {
        return Collections.unmodifiableSet(vanillaOreDict.getOrDefault(type, Collections.emptySet()));
    }
}