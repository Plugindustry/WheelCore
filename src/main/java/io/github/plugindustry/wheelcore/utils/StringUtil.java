package io.github.plugindustry.wheelcore.utils;

import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StringUtil {
    @Nonnull
    public static String key2Str(@Nonnull NamespacedKey key) {
        return "%s:%s".formatted(key.getNamespace(), key.getKey());
    }

    @Nullable
    public static NamespacedKey str2Key(@Nonnull String str) {
        int index = str.indexOf(':');
        if (index == -1) return null;
        return new NamespacedKey(str.substring(0, index), str.substring(index + 1));
    }
}