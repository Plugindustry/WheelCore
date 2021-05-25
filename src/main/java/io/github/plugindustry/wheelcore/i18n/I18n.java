package io.github.plugindustry.wheelcore.i18n;

import io.github.plugindustry.wheelcore.WheelCore;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class I18n {
    private static LinkedList<ResourceBundle> locales;

    public static void load(InputStreamReader reader) {
        try {
            locales.add(new PropertyResourceBundle(reader));
        } catch (IOException e) {
            e.printStackTrace();
            WheelCore.instance.getLogger().log(Level.SEVERE, "Error loading lang file");
        }
    }

    public static String getLocaleString(String key) {
        return locales.stream()
                .filter(bundle -> bundle.containsKey(key))
                .map(bundle -> bundle.getString(key))
                .findFirst()
                .orElse(key);
    }

    public static String[] getLocaleStringArray(String key) {
        return locales.stream()
                .filter(bundle -> bundle.containsKey(key))
                .map(bundle -> bundle.getStringArray(key))
                .findFirst()
                .orElse(new String[]{key});
    }
}
