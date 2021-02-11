package io.github.plugindustry.wheelcore.task;

import io.github.plugindustry.wheelcore.ConstItems;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.manager.ConfigManager;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.DebuggingLogger;
import org.bukkit.Bukkit;

public class AfterLoadTask implements Runnable {
    @Override
    public void run() {
        ConfigManager.init(WheelCore.instance);


        DebuggingLogger.debug("register blocks");
        RegisterTask.registerBlock();
        ConfigManager.loadBlocks();

        // Register recipes, blocks
        DebuggingLogger.debug("register items");
        RegisterTask.registerItem();
        ConstItems.initConst();
        DebuggingLogger.debug("register recipes");
        RegisterTask.registerRecipes();

        // Register tick
        Bukkit.getScheduler().runTaskTimer(WheelCore.instance, MainManager::update, 0, 0);

        /*DebuggingLogger.debug("Register Industrial crafting table.");
        Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(IndustrialWorld.instance, "crafting_table_craft"),
                                          ConstItems.IW_CRAFTING_TABLE).shape("AAA", "ABA", "AAA")
                                 .setIngredient('A', Material.IRON_INGOT)
                                 .setIngredient('B', Material.CRAFTING_TABLE));*/
    }
}