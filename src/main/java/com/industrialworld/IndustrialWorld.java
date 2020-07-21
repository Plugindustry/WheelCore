package com.industrialworld;

import com.industrialworld.commands.IndustrialWorldCommand;
import com.industrialworld.manager.ConfigManager;
import com.industrialworld.task.AfterLoadTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class IndustrialWorld extends JavaPlugin {
    public static IndustrialWorld instance;
    public static String serverVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

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

        // Register command, EventListener
        this.getCommand("iw").setExecutor(new IndustrialWorldCommand());

        // Register crafting table recipe
        Bukkit.getScheduler().runTask(this, new AfterLoadTask());
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
