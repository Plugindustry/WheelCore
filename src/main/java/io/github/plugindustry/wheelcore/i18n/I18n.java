package io.github.plugindustry.wheelcore.i18n;

import io.github.plugindustry.wheelcore.WheelCore;

import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class I18n {
    private static ResourceBundle bundle;

    public static void load(InputStreamReader reader) {
        try {
            bundle = new PropertyResourceBundle(reader);
        } catch (Exception e) {
            e.printStackTrace();
            WheelCore.instance.getLogger().log(Level.SEVERE, "Error loading lang file");
        }
    }

    public static String getLocaleString(String key) {
        return bundle.containsKey(key) ? bundle.getString(key) : key;
    }
}
