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
            Bukkit.getScheduler()
                  .runTaskTimer(WheelCore.instance, MainManager::save, ConfigManager.autoSaveDelay * 20L,
                          ConfigManager.autoSaveDelay * 20L);
        }
    }
}