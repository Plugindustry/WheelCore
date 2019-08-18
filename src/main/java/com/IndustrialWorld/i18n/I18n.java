package com.IndustrialWorld.i18n;

import com.IndustrialWorld.IndustrialWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.lang.Class;

public class I18n {
    public static String localeString = IndustrialWorld.localeString;
    public static File dataFolder = IndustrialWorld.instance.getDataFolder();

    private static ResourceBundle bundle;
    private static BufferedInputStream inputStream;

    static {
        String langFilePath = dataFolder.getAbsolutePath() + "\\lang\\" + localeString + ".lang";

        File langDir = new File(dataFolder.getAbsolutePath() +  "\\lang\\");
        if (!langDir.exists()) langDir.mkdir();
        File langFile = new File(dataFolder.getAbsolutePath() +  "\\lang\\" + localeString + ".lang");
        if (!(langFile.isFile())) {
            try {
                langFile.createNewFile();
                Properties prop = new Properties();
                InputStream in = null;
                FileOutputStream oFile = null;
                try {
                    in = new BufferedInputStream(IndustrialWorld.class.getResourceAsStream("/lang/" + localeString + ".lang"));
                    prop.load(new InputStreamReader(in, StandardCharsets.UTF_8));

                    oFile = new FileOutputStream(langFile, false);

                    prop.store(new OutputStreamWriter(oFile, StandardCharsets.UTF_8),null);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    if (oFile != null) {
                        try {
                            oFile.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //langFileInput = IndustrialWorld.class.getResourceAsStream("/lang/" + localeString + ".lang");
        //langFileOutput = new FileOutputStream(langFile);

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
