package io.github.plugindustry.wheelcore.command.sub;

import io.github.plugindustry.wheelcore.command.selector.EntitySelector;
import io.github.plugindustry.wheelcore.manager.ItemMapping;
import io.github.plugindustry.wheelcore.utils.StringUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GiveCommand extends SubCommandBase {
    @Override
    public String getPermissionRequired() {
        return "wheelcore.command.give";
    }

    @Override
    protected String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Give you or another player(s) an item.";
    }

    @Override
    public String getUsage() {
        return "give <player> <item>";
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (args.length != 2) {
            return false;
        }

        EntitySelector selector = new EntitySelector(commandSender, args[0]);
        String itemName = args[1];
        NamespacedKey itemId = StringUtil.str2Key(itemName);

        if (itemId == null) {
            commandSender.sendMessage("Illegal name.");
            return true;
        }

        if (!ItemMapping.isItemExists(itemId)) {
            commandSender.sendMessage("Uh we didn't found that item.");
            return true;
        }

        for (Entity e : selector.getSelectedEntity()) {
            if (!(e instanceof Player p)) {
                continue; // not a player >_> how to give them item.
            }
            p.getInventory().addItem(ItemMapping.get(itemId));
            commandSender.sendMessage("Given " + itemName + " to player " + p.getName());
        }

        return true;
    }
}