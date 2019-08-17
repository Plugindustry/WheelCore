package com.IndustrialWorld.i18n;

import com.IndustrialWorld.IndustrialWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.lang.Class;

public class I18n {
    public static String localeString = IndustrialWorld.localeString;
    public static File dataFolder = IndustrialWorld.instance.getDataFolder();

    private static ResourceBundle bundle;
    private static BufferedInputStream inputStream;
    private static InputStream langFileInput;
    private static FileOutputStream langFileOutput;

    static {
        String langFilePath = dataFolder.getAbsolutePath() + "\\lang\\" + localeString + ".lang";
        try {
            inputStream = new BufferedInputStream(new FileInputStream(langFilePath));
            bundle = new PropertyResourceBundle(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File langFile = new File(dataFolder.getAbsolutePath() +  "\\lang\\" + localeString + ".lang");
        if (!(langFile.isFile())) {
            try {
                langFile.createNewFile();
                langFileInput = IndustrialWorld.class.getResourceAsStream("/lang/" + localeString + ".lang");
                langFileOutput = new FileOutputStream(langFile);

                byte[] b = new byte[1024];
                int length;
                while((length = langFileInput.read(b))>0){
                    langFileOutput.write(b,0,length);
                }

                langFileInput.close();
                langFileOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getLocaleString(String key) {
        return bundle.getString(key);
    }
}
