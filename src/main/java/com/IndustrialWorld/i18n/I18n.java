package com.IndustrialWorld.i18n;

import com.IndustrialWorld.IndustrialWorld;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class I18n {
    Locale locale = IndustrialWorld.locale;

    private static ResourceBundle bundle;
    private static BufferedInputStream inputStream;

    static {
        String proFilePath = JavaPlugin.getPlugin().getDataFolder() + "\\lang\\";
        try {
            inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
            bundle = new PropertyResourceBundle(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
