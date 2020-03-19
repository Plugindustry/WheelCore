package com.industrialworld.commands.sub;

import com.industrialworld.commands.selector.EntitySelector;
import com.industrialworld.manager.ItemManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GiveCommand extends SubCommandBase {
    @Override
    public String getPermissionRequired() {
        return "industrialworld.give";
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

        if (!ItemManager.isItemExists(args[1])) {
            commandSender.sendMessage("Uh we didn't found that item.");
            return true;
        }

        for (Entity e : selector.getSelectedEntity()) {
            if (!(e instanceof Player)) {
                continue; // not a player >_> how to give them item.
            }
            Player p = (Player) e;
            p.getInventory().addItem(ItemManager.get(args[1]));
            commandSender.sendMessage("Given " + args[1] + " to player " + p.getName());
        }

        return true;
    }
}
