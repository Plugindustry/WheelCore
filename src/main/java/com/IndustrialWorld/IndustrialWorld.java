package com.IndustrialWorld;

import com.IndustrialWorld.event.EventListener;
import com.IndustrialWorld.event.TickEvent;
import com.IndustrialWorld.commands.IndustrialWorldCommand;
import com.IndustrialWorld.manager.ConfigManager;
import com.IndustrialWorld.manager.RegisterManager;
import com.IndustrialWorld.task.IWCraftingTableRegistrationTask;
import com.IndustrialWorld.utils.DebuggingLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class IndustrialWorld extends JavaPlugin {
    public static IndustrialWorld instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("|_   _|        | |         | |      (_)     | | | |  | |          | |   | |\n" +
                "  | | _ __   __| |_   _ ___| |_ _ __ _  __ _| | | |  | | ___  _ __| | __| |\n" +
                "  | || '_ \\ / _` | | | / __| __| '__| |/ _` | | | |/\\| |/ _ \\| '__| |/ _` |\n" +
                " _| || | | | (_| | |_| \\__ \\ |_| |  | | (_| | | \\  /\\  / (_) | |  | | (_| |\n" +
                " \\___/_| |_|\\__,_|\\__,_|___/\\__|_|  |_|\\__,_|_|  \\/  \\/ \\___/|_|  |_|\\__,_|\n" +
                "                                                                           ");

        instance = this;

        // Register command, EventListener and load config
        this.getCommand("iw").setExecutor(new IndustrialWorldCommand(this));
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        ConfigManager.init(this);
        // Register recipes, blocks
        DebuggingLogger.debug("register items");
        RegisterManager.registerItem();
        DebuggingLogger.debug("register recipes");
        RegisterManager.registerIWCRecipes();
        DebuggingLogger.debug("register blocks");
        RegisterManager.registerBlockIS();
        RegisterManager.registerIWItemMaterial();
        RegisterManager.registerIWMaterial();

        // Register TickEvent
        getServer().getScheduler().runTaskTimer(this, () -> getServer().getPluginManager().callEvent(new TickEvent()), 0, 0);

        // Register crafting table recipe
        getServer().getScheduler().runTask(this, new IWCraftingTableRegistrationTask(this));
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
