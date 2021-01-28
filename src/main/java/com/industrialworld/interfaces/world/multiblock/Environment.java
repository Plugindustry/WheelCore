package com.industrialworld.interfaces.world.multiblock;

import com.industrialworld.manager.MultiBlockManager;

public interface Environment {
    static Environment createDefaultEnvironment() {
        return new MultiBlockManager.SimpleEnvironment();
    }

    // Contains unchecked type cast, be careful to use it
    <T> T getEnvironmentArg(String key);

    <T> void setEnvironmentArg(String key, T value);
}
