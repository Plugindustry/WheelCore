package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.WheelCore;

public class DebuggingLogger {
    private static boolean DEBUGGING = true;

    public static void debug(String msg) {
        if (DEBUGGING) {
            WheelCore.instance.getLogger().info("DEBUG: " + msg);
        }
    }
}
