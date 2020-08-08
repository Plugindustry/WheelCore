package com.industrialworld.task;

import com.industrialworld.ConstItems;
import com.industrialworld.IndustrialWorld;
import com.industrialworld.manager.ConfigManager;
import com.industrialworld.manager.MainManager;
import com.industrialworld.manager.RegisterManager;
import com.industrialworld.utils.DebuggingLogger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class AfterLoadTask implements Runnable {
    @Override
    public void run() {
        ConfigManager.init(IndustrialWorld.instance);

        RegisterManager.registerMaterial();

        DebuggingLogger.debug("register blocks");
        RegisterManager.registerBlockIS();
        ConfigManager.loadBlocks();

        // Register recipes, blocks
        RegisterManager.registerIWItemMaterial();
        RegisterManager.registerIWMaterial();
        DebuggingLogger.debug("register items");
        RegisterManager.registerItem();
        DebuggingLogger.debug("register recipes");
        RegisterManager.registerIWCRecipes();

        // Register tick
        Bukkit.getScheduler().runTaskTimer(IndustrialWorld.instance, MainManager::update, 0, 0);

        DebuggingLogger.debug("Register Industrial crafting table.");
        Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(IndustrialWorld.instance, "crafting_table_craft"), ConstItems.IW_CRAFTING_TABLE).shape("AAA", "ABA", "AAA").setIngredient('A', Material.IRON_INGOT).setIngredient('B', Material.CRAFTING_TABLE));
    }
}