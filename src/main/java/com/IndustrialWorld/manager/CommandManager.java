package com.IndustrialWorld.manager;

import com.IndustrialWorld.IndustrialWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {
    private final IndustrialWorld plugin;

    public CommandManager(IndustrialWorld plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("iw")) {
            switch (args[0]) {
                case "test":
                    return true;
            }
            return true;
        }
        return false;
    }
}
