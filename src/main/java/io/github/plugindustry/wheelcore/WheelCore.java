package io.github.plugindustry.wheelcore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.plugindustry.wheelcore.command.WheelCoreCommand;
import io.github.plugindustry.wheelcore.event.EventListener;
import io.github.plugindustry.wheelcore.i18n.I18n;
import io.github.plugindustry.wheelcore.internal.overwrite.OverwriteRegistry;
import io.github.plugindustry.wheelcore.internal.shadow.ShadowRegistry;
import io.github.plugindustry.wheelcore.manager.ConfigManager;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.manager.PlayerDigHandler;
import io.github.plugindustry.wheelcore.manager.TextureManager;
import io.github.plugindustry.wheelcore.task.AfterLoadTask;
import io.github.plugindustry.wheelcore.task.RegisterTask;
import io.github.plugindustry.wheelcore.utils.GsonHelper;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public final class WheelCore extends JavaPlugin {
    private static WheelCore instance;
    private static ProtocolManager protocolManager;

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static WheelCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Startup logo
        getLogger().info("""

                __          ___               _  _____              \s
                \\ \\        / / |             | |/ ____|             \s
                 \\ \\  /\\  / /| |__   ___  ___| | |     ___  _ __ ___\s
                  \\ \\/  \\/ / | '_ \\ / _ \\/ _ \\ | |    / _ \\| '__/ _ \\
                   \\  /\\  /  | | | |  __/  __/ | |___| (_) | | |  __/
                    \\/  \\/   |_| |_|\\___|\\___|_|\\_____\\___/|_|  \\___|
                                                                    \s
                                                                    \s""".indent(1));

        instance = this;

        // Preheat
        protocolManager = ProtocolLibrary.getProtocolManager();
        OverwriteRegistry.init();
        ShadowRegistry.init();
        OverwriteRegistry.postInit();

        // Register command, EventListener
        Objects.requireNonNull(this.getCommand("wheelcore")).setExecutor(new WheelCoreCommand());

        RegisterTask.registerGenerator();

        // Register EventListener
        Bukkit.getPluginManager().registerEvents(new EventListener(), WheelCore.getInstance());
        Bukkit.getPluginManager().registerEvents(new I18n.EventListener(), WheelCore.getInstance());

        // Register PacketListener
        getProtocolManager().addPacketListener(new PlayerDigHandler.PacketListener());
        getProtocolManager().addPacketListener(new I18n.PacketListener());
        getProtocolManager().addPacketListener(new TextureManager.PacketListener());

        // Register GsonHelper.TypedNumber
        ConfigurationSerialization.registerClass(GsonHelper.TypedNumber.class);

        // Load config
        ConfigManager.init();

        // Register blocks, items, recipes
        RegisterTask.registerBlock();
        RegisterTask.registerItem();
        RegisterTask.registerRecipes();
        RegisterTask.registerVanillaOreDict();

        // Schedule the task that starts on the post-load stage
        Bukkit.getScheduler().runTask(this, new AfterLoadTask());

        Filter oldFilter = Bukkit.getLogger().getFilter();
        Bukkit.getLogger().setFilter(new Filter() {
            private final Filter old = oldFilter;

            @Override
            public boolean isLoggable(LogRecord record) {
                return (old == null || old.isLoggable(record)) && !record.getMessage()
                        .equals("A manual (plugin-induced) save has been detected while server is configured to auto-save. This may affect performance.") &&
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