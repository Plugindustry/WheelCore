package com.IndustrialWorld.manager;

import com.IndustrialWorld.IndustrialWorld;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                    sender.sendMessage("Tested successfully.");
                    return true;
                case "give":
                    if(args.length==2)
                        if(ItemManager.isItemExists(args[2]))
                            if(!args[1].startsWith("@"))
                                if(Bukkit.getPlayerExact(args[1])!=null)
                                   Bukkit.getPlayerExact(args[1]).getInventory().addItem(ItemManager.get(args[2]));
                                else sender.sendMessage("The specified player was not found.");
                            else if(args[1] == "@s" || args[1] == "@p")
                                if(sender instanceof Player)
                                ((Player)sender).getInventory().addItem(ItemManager.get(args[2]));
                            else if(args[1] == "@a")
                                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                                    p.getInventory().addItem(ItemManager.get(args[2]));
                                }
                            else sender.sendMessage("Unrecognized selector.");
                        else sender.sendMessage("The specified item was not found.This command can only give IndustrialWorld items, for vanilla item, use vanilla /give instead.");
                    else sender.sendMessage("Too many or too few arguments.");
                    return false;
                default:
                    sender.sendMessage("Invalid arguments.");
                    break;
            }
            return true;
        }
        return false;
    }
}
