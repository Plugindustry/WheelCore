package com.IndustrialWorld.commands;

import com.IndustrialWorld.IndustrialWorld;
import com.IndustrialWorld.commands.sub.GiveCommand;
import com.IndustrialWorld.commands.sub.SubCommandBase;
import com.IndustrialWorld.commands.sub.TestCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IndustrialWorldCommand implements CommandExecutor {
    private final IndustrialWorld plugin;

    public IndustrialWorldCommand(IndustrialWorld plugin) {
        this.plugin = plugin;
        new TestCommand();
        new GiveCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (args.length == 0) {
    		return false;
	    }

	    SubCommandBase scb = SubCommandBase.getCommand(args[0]);
		if (scb == null) {
			return false;
		}
    	String[] subArgs = new String[args.length - 1];
    	System.arraycopy(args, 1, subArgs, 0, args.length - 1);

    	if (!sender.hasPermission(scb.getPermissionRequired())) {
    		sender.sendMessage(ChatColor.RED + "You do not have the permission");
    		return true;
	    }
    	if (!scb.execute(sender, subArgs)) {
    		sender.sendMessage(scb.getUsage() + " - " + scb.getDescription());
    		return true;
	    }

        return true;
    }
}
