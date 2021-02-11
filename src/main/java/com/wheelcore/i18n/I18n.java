package com.wheelcore.i18n;

import com.wheelcore.IndustrialWorld;
import com.wheelcore.utils.ItemStackUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class I18n {
    public static String localeString;
    public static File dataFolder = IndustrialWorld.instance.getDataFolder();

    private static ResourceBundle bundle;

    public static void init(YamlConfiguration config) {
        localeString = config.getString("lang");
        File langDir = new File(dataFolder, "lang");
        if (langDir.isDirectory() || langDir.mkdir())
            ;
        File langFile = new File(langDir, localeString + ".lang");

        try (BufferedInputStream inputStream = new BufferedInputStream(langFile.isFile() ?
                                                                       new FileInputStream(langFile) :
                                                                       IndustrialWorld.class.getResourceAsStream(
                                                                               "/lang/" + localeString + ".lang"))) {
            bundle = new PropertyResourceBundle(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            IndustrialWorld.instance.getLogger().log(Level.SEVERE, "Error while reading lang file");
            IndustrialWorld.instance.getServer().getPluginManager().disablePlugin(IndustrialWorld.instance);
        }
    }

    public static ItemStackUtil.ItemStackFactory processItem(String id, ItemStackUtil.ItemStackFactory factory) {
        return processItemLore(id, processItemName(id, factory.setId(id)));
    }

    public static ItemStackUtil.ItemStackFactory processItem(String id, ItemStackUtil.ItemStackFactory factory, List<String> prefix) {
        return processItemLore(id, processItemName(id, factory.setId(id)), prefix);
    }

    public static ItemStackUtil.ItemStackFactory processItem(String id, ItemStackUtil.ItemStackFactory factory, List<String> prefix, List<String> suffix) {
        return processItemLore(id, processItemName(id, factory.setId(id)), prefix, suffix);
    }

    public static ItemStackUtil.ItemStackFactory processItemName(String id, ItemStackUtil.ItemStackFactory factory) {
        String lowerId = id.toLowerCase(Locale.ENGLISH);
        factory.setDisplayName(getLocaleString(String.format(I18nConst.Item.ITEM_NAME, lowerId)));
        return factory;
    }

    public static ItemStackUtil.ItemStackFactory processItemLore(String id, ItemStackUtil.ItemStackFactory factory) {
        return processItemLore(id, factory, Collections.emptyList(), Collections.emptyList());
    }

    public static ItemStackUtil.ItemStackFactory processItemLore(String id, ItemStackUtil.ItemStackFactory factory, List<String> prefix) {
        return processItemLore(id, factory, prefix, Collections.emptyList());
    }

    public static ItemStackUtil.ItemStackFactory processItemLore(String id, ItemStackUtil.ItemStackFactory factory, List<String> prefix, List<String> suffix) {
        String lowerId = id.toLowerCase(Locale.ENGLISH);
        LinkedList<String> tempList = new LinkedList<>();
        tempList.addAll(prefix);
        tempList.addAll(getLocaleStringList(String.format(I18nConst.Item.ITEM_LORE, lowerId)));
        tempList.addAll(suffix);
        factory.setLore(tempList);
        return factory;
    }

    public static String getLocaleString(String key) {
        return bundle.containsKey(key) ? bundle.getString(key) : key;
    }

    public static List<String> getLocaleStringList(String key) {
        return bundle.containsKey(key) ? Arrays.asList(bundle.getStringArray(key)) : Collections.singletonList(key);
    }
}
