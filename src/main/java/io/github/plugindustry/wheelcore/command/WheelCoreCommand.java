package io.github.plugindustry.wheelcore.command;

import io.github.plugindustry.wheelcore.command.sub.DebugCommand;
import io.github.plugindustry.wheelcore.command.sub.GiveCommand;
import io.github.plugindustry.wheelcore.command.sub.SubCommandBase;
import io.github.plugindustry.wheelcore.command.sub.TestCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public class WheelCoreCommand implements CommandExecutor {
    public WheelCoreCommand() {
        new TestCommand();
        new GiveCommand();
        new DebugCommand();
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label,
                             @Nonnull String[] args) {
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
