package io.github.plugindustry.wheelcore.manager;

import io.github.plugindustry.wheelcore.WheelCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {
    private static YamlConfiguration config;

    public static void init(WheelCore pl) {
        File dataFolder = pl.getDataFolder();

        if (dataFolder.isDirectory() || dataFolder.mkdirs())
            ;

        File config_yml = new File(dataFolder, "config.yml");
        if (!(config_yml.isFile())) {
            pl.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(config_yml);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
}
