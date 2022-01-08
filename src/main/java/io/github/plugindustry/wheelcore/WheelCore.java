package io.github.plugindustry.wheelcore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.plugindustry.wheelcore.command.WheelCoreCommand;
import io.github.plugindustry.wheelcore.event.EventListener;
import io.github.plugindustry.wheelcore.manager.ConfigManager;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.task.AfterLoadTask;
import io.github.plugindustry.wheelcore.task.RegisterTask;
import io.github.plugindustry.wheelcore.utils.DebuggingLogger;
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
    public static ProtocolManager protocolManager;

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

        // Preheat
        protocolManager = ProtocolLibrary.getProtocolManager();

        // Register command, EventListener
        Objects.requireNonNull(this.getCommand("wheelcore")).setExecutor(new WheelCoreCommand());

        RegisterTask.registerGenerator();

        // Register EventListener
        Bukkit.getPluginManager().registerEvents(new EventListener(), WheelCore.instance);

        // Load config
        ConfigManager.init();

        // Register blocks, items, recipes
        DebuggingLogger.debug("register blocks");
        RegisterTask.registerBlock();
        DebuggingLogger.debug("register items");
        RegisterTask.registerItem();
        DebuggingLogger.debug("register recipes");
        RegisterTask.registerRecipes();

        // Schedule the task that starts on the post-load stage
        Bukkit.getScheduler().runTask(this, new AfterLoadTask());

        Filter oldFilter = Bukkit.getLogger().getFilter();
        Bukkit.getLogger().setFilter(new Filter() {
            private final Filter old = oldFilter;

            @Override
            public boolean isLoggable(LogRecord record) {
                return (old == null || old.isLoggable(record)) && !record.getMessage().equals(
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
                MainManager.blockDataProvider.unloadChunk(chunk);
    }
}
