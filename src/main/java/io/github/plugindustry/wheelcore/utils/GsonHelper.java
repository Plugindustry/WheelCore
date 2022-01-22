package io.github.plugindustry.wheelcore.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.LinkedHashMap;
import java.util.Map;

public class GsonHelper {
    public static final JsonSerializer<?> POLYMORPHISM_SERIALIZER = (obj, type, jsonSerializationContext) -> {
        JsonObject result = new JsonObject();
        result.addProperty("type", obj.getClass().getName());
        result.add("data", jsonSerializationContext.serialize(obj, obj.getClass()));
        return result;
    };
    public static final JsonDeserializer<?> POLYMORPHISM_DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        try {
            return jsonDeserializationContext.deserialize(jsonObject.get("data"),
                                                          Class.forName(jsonObject.get("type").getAsString()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    };

    private static final JsonSerializer<ConfigurationSerializable> CONFIGURATION_SERIALIZABLE_SERIALIZER = (configurationSerializable, type, jsonSerializationContext) -> {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
                ConfigurationSerialization.getAlias(configurationSerializable.getClass()));
        map.putAll(configurationSerializable.serialize());
        return jsonSerializationContext.serialize(map, new TypeToken<Map<String, Object>>() {
        }.getType());
    };
    private static final JsonDeserializer<ConfigurationSerializable> CONFIGURATION_SERIALIZABLE_DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> deserializeMap(
            jsonDeserializationContext.deserialize(jsonElement, new TypeToken<Map<String, Object>>() {
            }.getType()));

    private static ConfigurationSerializable deserializeMap(Map<String, Object> map) {
        map.entrySet().forEach(entry -> {
            if (entry.getValue() instanceof Map) {
                Map<?, ?> subMap = (Map<?, ?>) entry.getValue();
                if (subMap.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
                    entry.setValue(deserializeMap((Map<String, Object>) subMap));
            }
        });
        return ConfigurationSerialization.deserializeObject(map);
    }

    public static GsonBuilder bukkitCompact() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(ConfigurationSerializable.class, CONFIGURATION_SERIALIZABLE_SERIALIZER);
        builder.registerTypeHierarchyAdapter(ConfigurationSerializable.class, CONFIGURATION_SERIALIZABLE_DESERIALIZER);
        return builder;
    }
}
