package io.github.plugindustry.wheelcore.command.sub;

import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.command.CommandSender;

import java.util.Objects;

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
            MainManager.blockDataProvider.blocks().forEach(b -> commandSender.sendMessage("World:" +
                    Objects.requireNonNull(b.getWorld())
                           .getName() +
                    " X:" +
                    b.getX() +
                    " Y:" +
                    b.getY() +
                    " Z:" +
                    b.getZ() +
                    " ID:" +
                    MainManager.getIdFromInstance(
                            MainManager.getBlockInstance(
                                    b)) +
                    " Instance:" +
                    MainManager.getBlockInstance(b)));
        else if (args[0].equals("instances")) {
            commandSender.sendMessage("Block:");
            MainManager.getBlockMapping().forEach((s, i) -> commandSender.sendMessage(s + " -- " + i));
            commandSender.sendMessage("Item:");
            MainManager.getItemMapping().forEach((s, i) -> commandSender.sendMessage(s + " -- " + i));
        } else
            return false;
        return true;
    }
}
