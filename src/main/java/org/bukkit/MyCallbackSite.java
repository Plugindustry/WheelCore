package org.bukkit;

import org.apache.commons.lang3.function.TriFunction;

public class MyCallbackSite {
    public static TriFunction<Object, Object, Object, Object> GetOverwrittenData;

    public static Object getOverwrittenData(Object world, Object blockPos, Object originData) {
        return GetOverwrittenData.apply(world, blockPos, originData);
    }
}