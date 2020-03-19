package com.industrialworld;

import com.industrialworld.commands.IndustrialWorldCommand;
import com.industrialworld.event.EventListener;
import com.industrialworld.event.TickEvent;
import com.industrialworld.manager.ConfigManager;
import com.industrialworld.manager.RegisterManager;
import com.industrialworld.task.IWCraftingTableRegistrationTask;
import com.industrialworld.utils.DebuggingLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class IndustrialWorld extends JavaPlugin {
    public static IndustrialWorld instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("\n|_   _|        | |         | |      (_)     | | | |  | |          | |   | |\n" +
                         "  | | _ __   __| |_   _ ___| |_ _ __ _  __ _| | | |  | | ___  _ __| | __| |\n" +
                         "  | || '_ \\ / _` | | | / __| __| '__| |/ _` | | | |/\\| |/ _ \\| '__| |/ _` |\n" +
                         " _| || | | | (_| | |_| \\__ \\ |_| |  | | (_| | | \\  /\\  / (_) | |  | | (_| |\n" +
                         " \\___/_| |_|\\__,_|\\__,_|___/\\__|_|  |_|\\__,_|_|  \\/  \\/ \\___/|_|  |_|\\__,_|\n" +
                         "                                                                           ");

        instance = this;

        // Register command, EventListener and load config
        this.getCommand("iw").setExecutor(new IndustrialWorldCommand());
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        ConfigManager.init(this);
        // Register recipes, blocks
        RegisterManager.registerIWItemMaterial();
        RegisterManager.registerIWMaterial();
        DebuggingLogger.debug("register items");
        RegisterManager.registerItem();
        DebuggingLogger.debug("register recipes");
        RegisterManager.registerIWCRecipes();
        DebuggingLogger.debug("register blocks");
        RegisterManager.registerBlockIS();

        // Register TickEvent
        getServer().getScheduler().runTaskTimer(this, () -> getServer().getPluginManager().callEvent(new TickEvent()), 0, 0);

        // Register crafting table recipe
        getServer().getScheduler().runTask(this, new IWCraftingTableRegistrationTask());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ConfigManager.shutdown();
    }

    public final String getDataFolderPath() {
        return getDataFolder().getAbsolutePath();
    }
}
