package com.IndustrialWorld;

import com.IndustrialWorld.blocks.BasicMachineBlock;
import com.IndustrialWorld.event.EventListener;
import com.IndustrialWorld.manager.BlockManager;
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
        File file = new File(getDataFolder(), "blocks.yml");
        if (!(file.isFile())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        BlockManager.loadBlocksFromConfig(config);

        BlockManager.register("BASIC_MACHINE_BLOCK", new BasicMachineBlock());
        getServer().addRecipe(new ShapedRecipe(new NamespacedKey(this, "BASIC_MACHINE_BLOCK"), ConstItems.BASIC_MACHINE_BLOCK).shape("AAA", "ABA", "AAA").setIngredient('A', Material.IRON_INGOT).setIngredient('B', Material.AIR));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BlockManager.saveBlocksToConfig(config);
        try {
            config.save(new File(getDataFolder(), "blocks.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
