package io.github.plugindustry.wheelcore.command.sub;

import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class DebugCommand extends SubCommandBase {
    @Override
    public String getPermissionRequired() {
        return "wheelcore.command.debug";
    }

    @Override
    protected String getName() {
        return "debug";
    }

    @Override
    public String getUsage() {
        return "debug blocks/instances";
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (args.length != 1)
            return false;

        if (args[0].equals("blocks"))
            MainManager.dataProvider.blocks().stream().map(Location::toString).forEach(commandSender::sendMessage);
        else if (args[0].equals("instances")) {
            commandSender.sendMessage("Block:");
            MainManager.getBlockMapping().keySet().forEach(commandSender::sendMessage);
            commandSender.sendMessage("Item:");
            MainManager.getItemMapping().keySet().forEach(commandSender::sendMessage);
        } else
            return false;
        return true;
    }
}
