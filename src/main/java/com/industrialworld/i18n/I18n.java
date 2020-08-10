package com.industrialworld.i18n;

import com.industrialworld.IndustrialWorld;
import com.industrialworld.utils.ItemStackUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public static ItemStackUtil.ItemStackFactory processItem(String id, int numOfLore, ItemStackUtil.ItemStackFactory factory) {
        return processItemLore(id, numOfLore, processItemName(id, factory.setId(id)));
    }

    public static ItemStackUtil.ItemStackFactory processItem(String id, int numOfLore, ItemStackUtil.ItemStackFactory factory, List<String> prefix) {
        return processItemLore(id, numOfLore, processItemName(id, factory.setId(id)), prefix);
    }

    public static ItemStackUtil.ItemStackFactory processItem(String id, int numOfLore, ItemStackUtil.ItemStackFactory factory, List<String> prefix, List<String> suffix) {
        return processItemLore(id, numOfLore, processItemName(id, factory.setId(id)), prefix, suffix);
    }

    public static ItemStackUtil.ItemStackFactory processItemName(String id, ItemStackUtil.ItemStackFactory factory) {
        String lowerId = id.toLowerCase(Locale.ENGLISH);
        factory.setDisplayName(getLocaleString(String.format(I18nConst.Item.ITEM_NAME, lowerId)));
        return factory;
    }

    public static ItemStackUtil.ItemStackFactory processItemLore(String id, int numOfLore, ItemStackUtil.ItemStackFactory factory) {
        return processItemLore(id, numOfLore, factory, Collections.emptyList(), Collections.emptyList());
    }

    public static ItemStackUtil.ItemStackFactory processItemLore(String id, int numOfLore, ItemStackUtil.ItemStackFactory factory, List<String> prefix) {
        return processItemLore(id, numOfLore, factory, prefix, Collections.emptyList());
    }

    public static ItemStackUtil.ItemStackFactory processItemLore(String id, int numOfLore, ItemStackUtil.ItemStackFactory factory, List<String> prefix, List<String> suffix) {
        String lowerId = id.toLowerCase(Locale.ENGLISH);
        factory.setLore(Stream.of(prefix.stream(),
                                  IntStream.rangeClosed(1, numOfLore)
                                          .mapToObj(i -> getLocaleString(String.format(I18nConst.Item.ITEM_LORE,
                                                                                       lowerId,
                                                                                       i))),
                                  suffix.stream()).flatMap(temp -> temp).collect(Collectors.toList()));
        return factory;
    }

    public static String getLocaleString(String key) {
        return bundle.containsKey(key) ? bundle.getString(key) : key;
    }
}
