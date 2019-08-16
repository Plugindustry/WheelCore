package com.IndustrialWorld.i18n;

import com.IndustrialWorld.IndustrialWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.lang.Class;

public class I18n {
    static String localeString = IndustrialWorld.localeString;
    static String dataFolder = IndustrialWorld.instance.getDataFolderPath();

    private static ResourceBundle bundle;
    private static BufferedInputStream inputStream;

    static {
        String langFilePath = dataFolder + "\\lang\\" + localeString + ".lang";
        try {
            inputStream = new BufferedInputStream(new FileInputStream(langFilePath));
            bundle = new PropertyResourceBundle(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLocaleString(String key) {
        return bundle.getString(key);
    }
}
