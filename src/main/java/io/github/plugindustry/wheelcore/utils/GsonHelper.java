package io.github.plugindustry.wheelcore.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import javax.annotation.Nonnull;
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
        map.entrySet().forEach(entry -> {
            if (entry.getValue() instanceof Integer) entry.setValue(new TypedNumber((Integer) entry.getValue()));
            else if (entry.getValue() instanceof Double) entry.setValue(new TypedNumber((Double) entry.getValue()));
            else if (entry.getValue() instanceof Float) entry.setValue(new TypedNumber((Float) entry.getValue()));
        });
        return jsonSerializationContext.serialize(map, new TypeToken<Map<String, Object>>() {
        }.getType());
    };
    private static final JsonDeserializer<ConfigurationSerializable> CONFIGURATION_SERIALIZABLE_DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> deserializeMap(
            jsonDeserializationContext.deserialize(jsonElement, new TypeToken<Map<String, Object>>() {
            }.getType()));

    @SuppressWarnings("unchecked")
    private static ConfigurationSerializable deserializeMap(Map<String, Object> map) {
        map.entrySet().forEach(entry -> {
            if (entry.getValue() instanceof Map<?, ?> subMap) {
                if (subMap.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                    ConfigurationSerializable temp = deserializeMap((Map<String, Object>) subMap);
                    entry.setValue(temp instanceof TypedNumber ? ((TypedNumber) temp).getNumber() : temp);
                }
            }
        });
        return ConfigurationSerialization.deserializeObject(map);
    }

    public static GsonBuilder bukkitCompat() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(ConfigurationSerializable.class, CONFIGURATION_SERIALIZABLE_SERIALIZER);
        builder.registerTypeHierarchyAdapter(ConfigurationSerializable.class, CONFIGURATION_SERIALIZABLE_DESERIALIZER);

        return builder;
    }

    public enum NumberType {
        INTEGER, DOUBLE, FLOAT
    }

    public static class TypedNumber implements ConfigurationSerializable {
        private final NumberType type;
        private final String number;

        public TypedNumber(Integer num) {
            type = NumberType.INTEGER;
            number = num.toString();
        }

        public TypedNumber(Double num) {
            type = NumberType.DOUBLE;
            number = num.toString();
        }

        public TypedNumber(Float num) {
            type = NumberType.FLOAT;
            number = num.toString();
        }

        public TypedNumber(Map<String, Object> map) {
            type = NumberType.valueOf((String) map.get("type"));
            number = (String) map.get("number");
        }

        public Number getNumber() {
            return switch (type) {
                case INTEGER -> Integer.valueOf(number);
                case DOUBLE -> Double.valueOf(number);
                case FLOAT -> Float.valueOf(number);
            };
        }

        @Nonnull
        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type.name());
            map.put("number", number);
            return map;
        }
    }
}