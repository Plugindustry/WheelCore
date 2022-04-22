package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.manager.ConfigManager;

public class DebuggingLogger {
    public static void debug(String msg) {
        if (ConfigManager.debug) WheelCore.getInstance().getLogger().info("DEBUG: " + msg);
    }
}