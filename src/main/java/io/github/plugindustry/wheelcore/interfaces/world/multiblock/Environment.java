package io.github.plugindustry.wheelcore.interfaces.world.multiblock;

import io.github.plugindustry.wheelcore.manager.MultiBlockManager;

public interface Environment {
    static Environment createDefaultEnvironment() {
        return new MultiBlockManager.SimpleEnvironment();
    }

    // Contains unchecked type cast, be careful to use it
    <T> T getEnvironmentArg(String key);

    <T> void setEnvironmentArg(String key, T value);
}
