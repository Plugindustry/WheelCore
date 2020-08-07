package com.industrialworld.command.sub;

import org.bukkit.command.CommandSender;

public class TestCommand extends SubCommandBase {
    @Override
    public String getPermissionRequired() {
        return "industrialworld.test";
    }

    @Override
    protected String getName() {
        return "test";
    }

    @Override
    public String getUsage() {
        return "test";
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        commandSender.sendMessage("Test passed 0w0");
        return true;
    }
}
