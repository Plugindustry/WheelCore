package io.github.plugindustry.wheelcore.manager;

import io.github.plugindustry.wheelcore.WheelCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {
    public static int autoSaveDelay;
    public static boolean debug;
    private static YamlConfiguration config;

    public static void init() {
        File dataFolder = WheelCore.instance.getDataFolder();

        if (!dataFolder.isDirectory()) {
            dataFolder.mkdirs();
        }

        File config_yml = new File(dataFolder, "config.yml");
        if (!(config_yml.isFile())) {
            WheelCore.instance.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(config_yml);

        autoSaveDelay = config.getInt("autosave-delay", 0);
        debug = config.getBoolean("debug", false);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
