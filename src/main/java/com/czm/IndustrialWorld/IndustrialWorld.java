package com.czm.IndustrialWorld;

import com.czm.IndustrialWorld.blocks.BasicMachineBlock;
import com.czm.IndustrialWorld.event.EventListener;
import com.czm.IndustrialWorld.manager.BlockManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class IndustrialWorld extends JavaPlugin {
    public static YamlConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        if (getDataFolder().isDirectory() || getDataFolder().mkdirs()) ;
        File file = new File(getDataFolder(), "config.yml");
        if (!(file.isFile()))
            saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(file);
        BlockManager.loadBlocksFromConfig(config);

        BlockManager.register("BASIC_MACHINE_BLOCK", new BasicMachineBlock());
        getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "BASIC_MACHINE_BLOCK"), ConstItems.BASIC_MACHINE_BLOCK).shape("A", "A", "A", "A", "B", "A", "A", "A", "A").setIngredient('A', Material.IRON_INGOT));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BlockManager.saveBlocksToConfig(config);
    }
}
