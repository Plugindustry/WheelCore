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
                    sender.sendMessage("Tested successfully");
                    return true;
                case "give":                    
                    sender.sendMessage("To do");
                    //ConstItems.
                    //if the item exists
                    return true;
                    //else return false
                default:
                    sender.sendMessage("Invalid arguments");
                    break;
            }
            return true;
        }
        return false;
    }
}
