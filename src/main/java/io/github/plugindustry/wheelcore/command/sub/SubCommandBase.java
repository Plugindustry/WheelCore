package io.github.plugindustry.wheelcore.command.sub;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class SubCommandBase {
    private static Map<String, SubCommandBase> commandMap = new HashMap<>();

    public SubCommandBase() {
        commandMap.put(getName(), this);
        for (String alias : getAliases()) {
            commandMap.put(alias, this);
        }
    }

    public static SubCommandBase getCommand(String cmd) {
        return commandMap.get(cmd);
    }

    protected String[] getAliases() {
        return new String[0];
    }

    public abstract String getPermissionRequired();

    protected abstract String getName();

    public abstract String getUsage();

    public String getDescription() {
        return "owo";
    }

    public abstract boolean execute(CommandSender commandSender, String[] args);

    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return new LinkedList<>();
    }
}
