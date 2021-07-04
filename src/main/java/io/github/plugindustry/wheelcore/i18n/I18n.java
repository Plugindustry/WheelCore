package io.github.plugindustry.wheelcore.i18n;

import io.github.plugindustry.wheelcore.WheelCore;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class I18n {
    private static final LinkedList<ResourceBundle> locales = new LinkedList<>();

    /**
     * @param reader Lang reader to load
     */
    public static void load(@Nonnull InputStreamReader reader) {
        try {
            locales.add(new PropertyResourceBundle(reader));
        } catch (IOException e) {
            e.printStackTrace();
            WheelCore.instance.getLogger().log(Level.SEVERE, "Error loading lang file");
        }
    }

    /**
     * @param key The key of the local string needed
     * @return The local string with the given key
     */
    @Nonnull
    public static String getLocaleString(@Nonnull String key) {
        return locales.stream()
                .filter(bundle -> bundle.containsKey(key))
                .map(bundle -> bundle.getString(key))
                .findFirst()
                .orElse(key);
    }

    /**
     * @param key The key of the local string list needed
     * @return The local string list with the given key
     * This list should be described in lang files like this:
     * listName[0]=xxx
     * listName[1]=xxx
     * ...
     * listName[n]=xxx
     */
    @Nonnull
    public static List<String> getLocaleStringList(@Nonnull String key) {
        return locales.stream().filter(bundle -> bundle.containsKey(key + "[0]")).map(bundle -> {
            List<String> list = new LinkedList<>();
            for (int index = 0; bundle.containsKey(key + "[" + index + "]"); ++index)
                list.add(bundle.getString(key + "[" + index + "]"));
            return list;
        }).findFirst().orElse(Collections.singletonList(key));
    }
}
