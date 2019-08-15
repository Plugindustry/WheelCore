package com.IndustrialWorld;

import com.IndustrialWorld.event.EventListener;
import com.IndustrialWorld.event.TickEvent;
import com.IndustrialWorld.manager.MainManager;
import com.IndustrialWorld.manager.RegisterManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class IndustrialWorld extends JavaPlugin {
    public static YamlConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        if (getDataFolder().isDirectory() || getDataFolder().mkdirs()) ;

        File file = new File(getDataFolder(), "config.yml");
        if (!(file.isFile())) {
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(file);

        File file2 = new File(getDataFolder(), "blocks.yml");
        if (!(file.isFile())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration block = YamlConfiguration.loadConfiguration(file2);
        MainManager.loadBlocksFromConfig(block);

        RegisterManager.registerIWCRecipes();
        RegisterManager.registerBlockIS();

        getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "IW_CRAFTING_TABLE"), ConstItems.IW_CRAFTING_TABLE).shape("AAA", "ABA", "AAA").setIngredient('A', Material.IRON_INGOT).setIngredient('B', Material.CRAFTING_TABLE));

        getServer().getScheduler().runTaskTimer(this, () -> getServer().getPluginManager().callEvent(new TickEvent()), 0, 0);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        MainManager.saveBlocksToConfig(config);
        try {
            config.save(new File(getDataFolder(), "blocks.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
