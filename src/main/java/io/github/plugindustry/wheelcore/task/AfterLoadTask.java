package io.github.plugindustry.wheelcore.task;

import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.manager.ConfigManager;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class AfterLoadTask implements Runnable {
    @Override
    public void run() {
        // Register tick
        Bukkit.getScheduler().runTaskTimer(WheelCore.instance, MainManager::update, 0, 0);

        // Register auto save
        if (ConfigManager.autoSaveDelay != 0) {
            for (World world : Bukkit.getWorlds())
                world.setAutoSave(false);
            Bukkit.getScheduler().runTaskTimer(WheelCore.instance,
                                               MainManager::save,
                                               ConfigManager.autoSaveDelay * 20L,
                                               ConfigManager.autoSaveDelay * 20L);
        }

        /*MultiBlockManager.register(MainManager.getBlockInstanceFromId("BASIC_MACHINE_BLOCK"), MultiBlockManager.Conditions.create()
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