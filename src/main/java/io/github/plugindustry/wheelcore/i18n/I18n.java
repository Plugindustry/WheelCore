package io.github.plugindustry.wheelcore.i18n;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.item.ItemData;
import io.github.plugindustry.wheelcore.manager.ConfigManager;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class I18n {
    private static final HashMap<Locale, Map<String, String>> locales = new HashMap<>();
    private static final Pattern pattern = Pattern.compile("\\{\\{(\\S*?)}}");
    private static final Pattern patternList = Pattern.compile("\\{\\{(\\S*?)\\[]}}");
    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new Gson();

    /**
     * @param reader Lang reader to load
     */
    public static void load(@Nonnull Locale locale, @Nonnull Reader reader) {
        try {
            Map<String, String> kvMap;
            if (locales.containsKey(locale))
                kvMap = locales.get(locale);
            else {
                kvMap = new HashMap<>();
                locales.put(locale, kvMap);
            }
            ResourceBundle bundle = new PropertyResourceBundle(reader);
            for (String key : bundle.keySet())
                kvMap.put(key, bundle.getString(key));
        } catch (IOException e) {
            e.printStackTrace();
            WheelCore.instance.getLogger().log(Level.SEVERE, "Error loading lang file");
        }
    }

    /**
     * @param key The key of the local string needed
     * @return The local string with the given key
     */
    @Nonnull
    public static String getLocaleString(@Nonnull Locale locale, @Nonnull String key) {
        Map<String, String> kvMap = locales.getOrDefault(locale, Collections.emptyMap());
        if (kvMap.containsKey(key))
            return kvMap.getOrDefault(key, key);
        else if (!ConfigManager.fallbackLang.equals(locale))
            return getLocaleString(ConfigManager.fallbackLang, key);
        else
            return key;
    }

    /**
     * @param key The key of the locale placeholder needed
     * @return The locale placeholder with the given key
     */
    @Nonnull
    public static String getLocalePlaceholder(@Nonnull String key) {
        return "{{" + key + "}}";
    }

    /**
     * @param key The key of the local string list needed
     * @return The local string list with the given key
     * If this key represents an array, this array should be described in lang files like this:
     * listName[0]=xxx
     * listName[1]=xxx
     * ...
     * listName[n]=xxx
     * and the returned value consists of all these values.
     * <p>
     * Otherwise, this call equals to Collections.singletonList({@link I18n#getLocaleString(Locale, String)}).
     */
    @Nonnull
    public static List<String> getLocaleStringList(@Nonnull Locale locale, @Nonnull String key) {
        Map<String, String> kvMap = locales.getOrDefault(locale, Collections.emptyMap());
        if (kvMap.containsKey(key + "[0]")) {
            List<String> list = new LinkedList<>();
            for (int index = 0; kvMap.containsKey(key + "[" + index + "]"); ++index)
                list.add(kvMap.get(key + "[" + index + "]"));
            return list;
        } else if (!ConfigManager.fallbackLang.equals(locale))
            return getLocaleStringList(ConfigManager.fallbackLang, key);
        else
            return Collections.singletonList(getLocaleString(locale, key));
    }

    /**
     * @param key The key of the locale placeholder list needed
     * @return The locale placeholder list with the given key
     * This list should be described in lang files like this:
     * listName[0]=xxx
     * listName[1]=xxx
     * ...
     * listName[n]=xxx
     */
    @Nonnull
    public static String getLocaleListPlaceholder(@Nonnull String key) {
        return "{{" + key + "[]}}";
    }

    @Nonnull
    public static String replaceAll(@Nonnull Locale locale, @Nonnull String str) {
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll(matchResult -> getLocaleString(locale, matchResult.group(1)));
    }

    @Nonnull
    public static List<String> replaceAllList(@Nonnull Locale locale, @Nonnull String str) {
        Matcher matcher = patternList.matcher(str);
        if (matcher.matches())
            return getLocaleStringList(locale, matcher.group(1));
        else
            return Collections.singletonList(replaceAll(locale, str));
    }

    @Nonnull
    public static JsonElement replaceAll(@Nonnull Locale locale, @Nonnull JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            JsonObject newJsonObject = new JsonObject();
            jsonObject.entrySet().forEach(e -> newJsonObject.add(e.getKey(), replaceAll(locale, e.getValue())));
            element = newJsonObject;
        } else if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            JsonArray newJsonArray = new JsonArray();
            for (int i = 0; i < jsonArray.size(); ++i)
                newJsonArray.add(replaceAll(locale, jsonArray.get(i)));
            element = newJsonArray;
        } else if (element.isJsonPrimitive() && ((JsonPrimitive) element).isString())
            element = new JsonPrimitive(replaceAll(locale, element.getAsString()));
        return element;
    }

    @Nonnull
    private static ItemStack translateItem(@Nonnull Locale locale, @Nonnull ItemStack item) {
        ItemStack newItem = item.clone();
        if (newItem.hasItemMeta()) {
            ItemMeta meta = Objects.requireNonNull(newItem.getItemMeta());
            if (meta.hasDisplayName())
                meta.setDisplayName(replaceAll(locale, meta.getDisplayName()));
            if (meta.hasLore())
                meta.setLore(Objects.requireNonNull(meta.getLore())
                                     .stream()
                                     .flatMap(str -> replaceAllList(locale, str).stream())
                                     .collect(Collectors.toList()));
            newItem.setItemMeta(meta);
            if (!(MainManager.getItemData(item) instanceof TranslatedItemData))
                MainManager.setItemData(newItem, new TranslatedItemData(item));
        }
        return newItem;
    }

    @Nonnull
    private static ItemStack getOriginalItem(@Nonnull ItemStack item) {
        ItemData itemData = MainManager.getItemData(item);
        if (itemData instanceof TranslatedItemData) {
            ItemStack orgItem = Optional.ofNullable(((TranslatedItemData) itemData).originalItem).orElse(item);
            orgItem.setAmount(item.getAmount());
            return orgItem;
        } else
            return item;
    }

    public static class TranslatedItemData implements ItemData {
        public ItemStack originalItem;

        public TranslatedItemData() {}

        public TranslatedItemData(ItemStack originalItem) {
            this.originalItem = originalItem.clone();
        }
    }

    public static class PacketListener extends PacketAdapter {
        private static final Set<PacketType> outTypes = new HashSet<>();

        static {
            for (PacketType type : PacketType.values())
                if (type.isServer() && type.isSupported() && !type.isDeprecated())
                    outTypes.add(type);
            outTypes.add(PacketType.Play.Client.SET_CREATIVE_SLOT);
        }

        public PacketListener() {
            super(PacketAdapter.params()
                          .clientSide()
                          .serverSide()
                          .plugin(WheelCore.instance)
                          .listenerPriority(ListenerPriority.LOW)
                          .types(outTypes));
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            Locale locale;
            try {
                locale = Locale.forLanguageTag(event.getPlayer().getLocale().replace('_', '-'));
            } catch (Exception exception) {
                return;
            }

            StructureModifier<String> strings = packet.getStrings();
            for (int i = 0; i < strings.size(); ++i)
                strings.modify(i, str -> replaceAll(locale, str));
            StructureModifier<String[]> stringArrays = packet.getStringArrays();
            for (int i = 0; i < stringArrays.size(); ++i)
                stringArrays.modify(i,
                                    strArr -> Arrays.stream(strArr)
                                            .map(str -> replaceAll(locale, str))
                                            .toArray(String[]::new));

            StructureModifier<WrappedChatComponent> chatComponents = packet.getChatComponents();
            for (int i = 0; i < chatComponents.size(); ++i)
                chatComponents.modify(i, chatComponent -> {
                    chatComponent.setJson(gson.toJson(replaceAll(locale, parser.parse(chatComponent.getJson()))));
                    return chatComponent;
                });
            StructureModifier<WrappedChatComponent[]> chatComponentArrays = packet.getChatComponentArrays();
            for (int i = 0; i < chatComponentArrays.size(); ++i)
                chatComponentArrays.modify(i, chatComponentArr -> {
                    for (WrappedChatComponent chatComponent : chatComponentArr)
                        chatComponent.setJson(gson.toJson(replaceAll(locale, parser.parse(chatComponent.getJson()))));
                    return chatComponentArr;
                });

            try {
                StructureModifier<ItemStack> itemStacks = packet.getItemModifier();
                for (int i = 0; i < itemStacks.size(); ++i)
                    itemStacks.modify(i, item -> translateItem(locale, item));
                StructureModifier<ItemStack[]> itemStacksArrays = packet.getItemArrayModifier();
                for (int i = 0; i < itemStacksArrays.size(); ++i)
                    itemStacksArrays.modify(i,
                                            itemArr -> Arrays.stream(itemArr)
                                                    .map(item -> translateItem(locale, item))
                                                    .toArray(ItemStack[]::new));
                StructureModifier<List<ItemStack>> itemStacksLists = packet.getItemListModifier();
                for (int i = 0; i < itemStacksLists.size(); ++i)
                    itemStacksLists.modify(i,
                                           itemList -> itemList.stream()
                                                   .map(item -> translateItem(locale, item))
                                                   .collect(Collectors.toList()));
            } catch (Exception ignored) {
            }

            event.setPacket(packet);
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            packet.getItemModifier().modify(0, I18n::getOriginalItem);
            event.setPacket(packet);
        }
    }
}
