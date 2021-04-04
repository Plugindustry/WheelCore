package io.github.plugindustry.wheelcore.manager;

import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.i18n.I18n;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private static YamlConfiguration blocksConfig;
    private static File dataFolder;
    private static YamlConfiguration config;
    private static YamlConfiguration worldGenConfig;

    public static void init(WheelCore pl) {
        dataFolder = pl.getDataFolder();

        if (dataFolder.isDirectory() || dataFolder.mkdirs())
            ;

        File config_yml = new File(dataFolder, "config.yml");
        if (!(config_yml.isFile())) {
            pl.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(config_yml);
        I18n.readNewFile(config.getString("lang"), dataFolder.getPath() + config.getString("lang") + ".lang");

        File world_generation_yml = new File(dataFolder, "world_generation.yml");
        if (!(world_generation_yml.isFile())) {
            try {
                world_generation_yml.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        worldGenConfig = YamlConfiguration.loadConfiguration(world_generation_yml);
    }

    public static void loadBlocks() {
        /*File block_yml = new File(dataFolder, "blocks.yml");
        if (!(block_yml.isFile())) {
            try {
                block_yml.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        blocksConfig = YamlConfiguration.loadConfiguration(block_yml);*/
        MainManager.loadBlocks();
    }

    public static void shutdown() {
        MainManager.saveBlocks();
        /*try {
            blocksConfig.save(new File(dataFolder, "blocks.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static YamlConfiguration getWorldGenConfig() {
        return worldGenConfig;
    }

    public static YamlConfiguration getBlocksConfig() {
        return blocksConfig;
    }
}
