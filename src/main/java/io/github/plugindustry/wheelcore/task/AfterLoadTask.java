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
        MainManager.loadBlocks();

        // Register recipes, blocks
        DebuggingLogger.debug("register items");
        RegisterTask.registerItem();
        ConstItems.initConst();
        DebuggingLogger.debug("register recipes");
        RegisterTask.registerRecipes();

        // Register tick
        Bukkit.getScheduler().runTaskTimer(WheelCore.instance, MainManager::update, 0, 0);

        /*MultiBlockManager.register(MainManager.getInstanceFromId("BASIC_MACHINE_BLOCK"), MultiBlockManager.Conditions.create()
                .then(Relocators.move(1, 0, 1))
                .check(Matchers.cube(9, 0, 9, Material.ACACIA_PLANKS))
                .then(Definers.scan("height", new Vector(0, 1, 0), Material.ACACIA_PLANKS, 10))
                .check(env -> env.<Integer>getEnvironmentArg("height") >= 3)
                .then(env -> env.setEnvironmentArg("height", env.<Integer>getEnvironmentArg("height") - 1))
                .check(Matchers.cube(9, "height", 0, Material.ACACIA_PLANKS))
                .check(Matchers.cube(0, "height", 9, Material.ACACIA_PLANKS)));*/

        /*DebuggingLogger.debug("Register Industrial crafting table.");
        Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(IndustrialWorld.instance, "crafting_table_craft"),
                                          ConstItems.IW_CRAFTING_TABLE).shape("AAA", "ABA", "AAA")
                                 .setIngredient('A', Material.IRON_INGOT)
                                 .setIngredient('B', Material.CRAFTING_TABLE));*/
    }
}