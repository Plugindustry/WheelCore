package io.github.plugindustry.wheelcore.i18n;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.*;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.item.ItemData;
import io.github.plugindustry.wheelcore.manager.ConfigManager;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private static final ConcurrentHashMap<UUID, Pair<Map<UUID, ItemStack>, Queue<UUID>>> orgItemMapping = new ConcurrentHashMap<>();
    private static final int ITEM_LIMIT = 512;

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
    private static ItemStack translateItem(@Nonnull Player player, @Nonnull Locale locale, @Nonnull ItemStack item) {
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
            if (!(MainManager.getItemData(item) instanceof TranslatedItemData)) {
                UUID uuid = UUID.randomUUID();
                MainManager.setItemData(newItem, new TranslatedItemData(uuid));
                if(!orgItemMapping.containsKey(player.getUniqueId())) orgItemMapping.put(player.getUniqueId(), Pair.of(new HashMap<>(), new ArrayDeque<>(ITEM_LIMIT)));
                Pair<Map<UUID, ItemStack>, Queue<UUID>> pair = orgItemMapping.get(player.getUniqueId());
                while(pair.second.size() >= ITEM_LIMIT)
                    pair.first.remove(pair.second.poll());
                pair.first.put(uuid, item.clone());
                pair.second.add(uuid);
            }
        }
        return newItem;
    }

    public static class TranslatedItemData implements ItemData {
        public UUID uuid;

        public TranslatedItemData(UUID uuid) {
            this.uuid = uuid;
        }
    }

    public static class PacketListener extends PacketAdapter {
        private static final Set<PacketType> outTypes = new HashSet<>();

        static {
            outTypes.add(PacketType.Play.Server.CHAT);
            outTypes.add(PacketType.Play.Server.TAB_COMPLETE);
            outTypes.add(PacketType.Play.Server.WINDOW_ITEMS);
            outTypes.add(PacketType.Play.Server.SET_SLOT);
            outTypes.add(PacketType.Play.Server.OPEN_WINDOW);
            outTypes.add(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
            outTypes.add(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
            outTypes.add(PacketType.Play.Server.SCOREBOARD_SCORE);
            outTypes.add(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
            outTypes.add(PacketType.Play.Server.SCOREBOARD_TEAM);
            outTypes.add(PacketType.Play.Server.SET_TITLE_TEXT);
            outTypes.add(PacketType.Play.Server.SET_SUBTITLE_TEXT);
            outTypes.add(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
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
            Player player = event.getPlayer();
            Locale locale;
            try {
                locale = Locale.forLanguageTag(player.getLocale().replace('_', '-'));
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
                    itemStacks.modify(i, item -> translateItem(player, locale, item));
                StructureModifier<ItemStack[]> itemStacksArrays = packet.getItemArrayModifier();
                for (int i = 0; i < itemStacksArrays.size(); ++i)
                    itemStacksArrays.modify(i,
                            itemArr -> Arrays.stream(itemArr)
                                    .map(item -> translateItem(player, locale, item))
                                    .toArray(ItemStack[]::new));
                StructureModifier<List<ItemStack>> itemStacksLists = packet.getItemListModifier();
                for (int i = 0; i < itemStacksLists.size(); ++i)
                    itemStacksLists.modify(i,
                            itemList -> itemList.stream()
                                    .map(item -> translateItem(player, locale, item))
                                    .collect(Collectors.toList()));
            } catch (Exception ignored) {
            }

            event.setPacket(packet);
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            int slot = packet.getIntegers().read(0);
            ItemStack item = packet.getItemModifier().read(0);
            UUID uuid = event.getPlayer().getUniqueId();
            if (item.getType() != Material.AIR) {
                ItemData data = MainManager.getItemData(item);
                if (data instanceof TranslatedItemData) {
                    if (orgItemMapping.containsKey(uuid) && orgItemMapping.get(uuid).first.containsKey(((TranslatedItemData) data).uuid)) {
                        ItemStack orgItem = orgItemMapping.get(uuid).first.get(((TranslatedItemData) data).uuid).clone();
                        orgItem.setAmount(item.getAmount());
                        packet.getItemModifier().write(0, orgItem);
                    } else packet.getItemModifier().write(0, new ItemStack(Material.AIR));
                    event.setPacket(packet);
                }
            }
        }
    }

    public static class EventListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent event) {
            orgItemMapping.remove(event.getPlayer().getUniqueId());
        }
    }
}
