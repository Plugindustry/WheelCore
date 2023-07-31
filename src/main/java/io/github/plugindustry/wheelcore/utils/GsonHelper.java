package io.github.plugindustry.wheelcore.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.plugindustry.wheelcore.WheelCore;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class GsonHelper {
    public static final JsonSerializer<?> POLYMORPHISM_SERIALIZER = (obj, type, jsonSerializationContext) -> {
        JsonObject result = new JsonObject();
        result.addProperty("type", obj.getClass().getName());
        result.add("data", jsonSerializationContext.serialize(obj, obj.getClass()));
        return result;
    };
    public static final JsonDeserializer<?> POLYMORPHISM_DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String clazz = jsonObject.get("type").getAsString();
        try {
            return jsonDeserializationContext.deserialize(jsonObject.get("data"), Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            WheelCore.getInstance().getLogger()
                    .log(Level.SEVERE, e, () -> "Error deserializing object of class %s".formatted(clazz));
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
            else if (entry.getValue() instanceof Long) entry.setValue(new TypedNumber((Long) entry.getValue()));
            else if (entry.getValue() instanceof Double) entry.setValue(new TypedNumber((Double) entry.getValue()));
            else if (entry.getValue() instanceof Float) entry.setValue(new TypedNumber((Float) entry.getValue()));
        });
        return jsonSerializationContext.serialize(map, new TypeToken<Map<String, Object>>() {
        }.getType());
    };
    private static final JsonDeserializer<ConfigurationSerializable> CONFIGURATION_SERIALIZABLE_DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> deserializeMap(
            jsonDeserializationContext.deserialize(jsonElement, new TypeToken<Map<String, Object>>() {
            }.getType()));
    private static final TypeAdapter<NamespacedKey> NAMESPACED_KEY_TYPE_ADAPTER = new TypeAdapter<>() {
        @Override
        public void write(JsonWriter jsonWriter, NamespacedKey namespacedKey) throws IOException {
            jsonWriter.value(namespacedKey.toString());
        }

        @Override
        public NamespacedKey read(JsonReader jsonReader) throws IOException {
            String s = jsonReader.nextString();
            return NamespacedKey.fromString(s);
        }
    };

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
        builder.registerTypeAdapter(NamespacedKey.class, NAMESPACED_KEY_TYPE_ADAPTER);

        return builder;
    }

    public enum NumberType {
        INTEGER, LONG, DOUBLE, FLOAT
    }

    public static class TypedNumber implements ConfigurationSerializable {
        private final NumberType type;
        private final String number;

        public TypedNumber(Integer num) {
            type = NumberType.INTEGER;
            number = num.toString();
        }

        public TypedNumber(Long num) {
            type = NumberType.LONG;
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
                case LONG -> Long.valueOf(number);
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