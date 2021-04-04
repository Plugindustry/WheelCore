package io.github.plugindustry.wheelcore.i18n;

import io.github.plugindustry.wheelcore.WheelCore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class I18n {
    private static Map<I18nLocale, Map<String, String>> localeMap;

    public static void readNewFile(I18nLocale locale, String path) {
        File langFile = new File(path);
        ResourceBundle bundle;

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(langFile))) {
            bundle = new PropertyResourceBundle(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            WheelCore.instance.getLogger().log(Level.SEVERE, "Can't read lang file at " + path);
            return;
        }

        Map<String, String> currentLocaleMap = localeMap.containsKey(locale) ? localeMap.get(locale) : new HashMap<>();

        for (String key : bundle.keySet()) {
            currentLocaleMap.put(key, bundle.getString(key));
        }

        localeMap.put(locale, currentLocaleMap);
    }

    public static void readNewFile(String localeName, String path) {
        readNewFile(getLocaleFromName(localeName), path);
    }

    public static String getLocaleString(I18nLocale locale, String key) {
        return localeMap.containsKey(locale) ? localeMap.get(locale).getOrDefault(key, key) : key;
    }

    private static Map<String, I18nLocale> nameLocaleMap = new HashMap<>();
    static {
        nameLocaleMap.put("en_US", I18nLocale.EN_US);
        nameLocaleMap.put("zh_CN", I18nLocale.ZH_CN);
    }

    public static I18nLocale getLocaleFromName(String name) {
        return nameLocaleMap.get(name);
    }
}
