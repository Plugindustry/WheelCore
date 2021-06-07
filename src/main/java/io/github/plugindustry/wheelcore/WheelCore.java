package io.github.plugindustry.wheelcore;

import io.github.plugindustry.wheelcore.command.WheelCoreCommand;
import io.github.plugindustry.wheelcore.event.EventListener;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.task.AfterLoadTask;
import io.github.plugindustry.wheelcore.task.RegisterTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public final class WheelCore extends JavaPlugin {
    public static final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public static WheelCore instance;

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
        Objects.requireNonNull(this.getCommand("wheelcore")).setExecutor(new WheelCoreCommand());

        RegisterTask.registerGenerator();

        // Register EventListener
        Bukkit.getPluginManager().registerEvents(new EventListener(), WheelCore.instance);

        // Register crafting table recipe
        Bukkit.getScheduler().runTask(this, new AfterLoadTask());

        Filter oldFilter = Bukkit.getLogger().getFilter();
        Bukkit.getLogger().setFilter(new Filter() {
            private final Filter old = oldFilter;

            @Override
            public boolean isLoggable(LogRecord record) {
                return (old == null || old.isLoggable(record)) &&
                       !record.getMessage().equals(
                               "A manual (plugin-induced) save has been detected while server is configured to auto-save. This may affect performance.") &&
                       !record.getMessage().endsWith(
                               "Bukkit will attempt to fix this, but there may be additional damage that we cannot recover.");
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (World world : Bukkit.getWorlds())
            for (Chunk chunk : world.getLoadedChunks())
                MainManager.dataProvider.unloadChunk(chunk);
    }
}
