package io.github.plugindustry.wheelcore.interfaces.world.multiblock;

import io.github.plugindustry.wheelcore.manager.MultiBlockManager;

import javax.annotation.Nonnull;

public interface Environment {
    @Nonnull
    static Environment createDefaultEnvironment() {
        return new MultiBlockManager.SimpleEnvironment();
    }

    /**
     * This method Contains unchecked type cast, use it at your own risk
     */
    <T> T getEnvironmentArg(String key);

    <T> void setEnvironmentArg(String key, T value);
}
