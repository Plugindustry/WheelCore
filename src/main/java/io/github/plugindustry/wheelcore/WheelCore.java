package io.github.plugindustry.wheelcore;

import io.github.plugindustry.wheelcore.command.WheelCoreCommand;
import io.github.plugindustry.wheelcore.event.EventListener;
import io.github.plugindustry.wheelcore.manager.ConfigManager;
import io.github.plugindustry.wheelcore.task.AfterLoadTask;
import io.github.plugindustry.wheelcore.task.RegisterTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class WheelCore extends JavaPlugin {
    public static WheelCore instance;
    public static String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Startup logo
        getLogger().info("\n __          ___               _  _____               \n" +
                         " \\ \\        / / |             | |/ ____|              \n" +
                         "  \\ \\  /\\  / /| |__   ___  ___| | |     ___  _ __ ___ \n" +
                         "   \\ \\/  \\/ / | '_ \\ / _ \\/ _ \\ | |    / _ \\| '__/ _ \\\n" +
                         "    \\  /\\  /  | | | |  __/  __/ | |___| (_) | | |  __/\n" +
                         "     \\/  \\/   |_| |_|\\___|\\___|_|\\_____\\___/|_|  \\___|\n" +
                         "                                                      \n" +
                         "                                                      ");

        instance = this;

        // Register command, EventListener
        this.getCommand("wheelcore").setExecutor(new WheelCoreCommand());

        RegisterTask.registerGenerator();

        // Register EventListener
        Bukkit.getPluginManager().registerEvents(new EventListener(), WheelCore.instance);

        // Register crafting table recipe
        Bukkit.getScheduler().runTask(this, new AfterLoadTask());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ConfigManager.shutdown();
    }
}
