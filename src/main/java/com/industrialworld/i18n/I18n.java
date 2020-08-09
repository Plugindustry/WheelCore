package com.industrialworld.i18n;

import com.industrialworld.IndustrialWorld;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class I18n {
    public static String localeString;
    public static File dataFolder = IndustrialWorld.instance.getDataFolder();

    private static ResourceBundle bundle;
    private static BufferedInputStream inputStream;

    public static void init(YamlConfiguration config) {
        localeString = config.getString("lang");
        File langDir = new File(dataFolder, "lang");
        if (langDir.isDirectory() || langDir.mkdir())
            ;
        File langFile = new File(langDir, localeString + ".lang");

        try {
            inputStream = new BufferedInputStream(langFile.isFile() ?
                                                  new FileInputStream(langFile) :
                                                  IndustrialWorld.class.getResourceAsStream("/lang/" +
                                                                                            localeString +
                                                                                            ".lang"));
            bundle = new PropertyResourceBundle(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            IndustrialWorld.instance.getLogger().log(Level.SEVERE, "Error while reading lang file");
            IndustrialWorld.instance.getServer().getPluginManager().disablePlugin(IndustrialWorld.instance);
        }
    }

    public static String getLocaleString(String key) {
        return new String(bundle.getString(key).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
