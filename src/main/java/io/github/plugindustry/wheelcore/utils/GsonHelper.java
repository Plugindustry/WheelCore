package io.github.plugindustry.wheelcore.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

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
    private static final JsonSerializer<Location> LOCATION_SERIALIZER = (location, type, jsonSerializationContext) -> {
        JsonObject result = new JsonObject();
        result.addProperty("x", location.getX());
        result.addProperty("y", location.getY());
        result.addProperty("z", location.getZ());
        result.addProperty("pitch", location.getPitch());
        result.addProperty("yaw", location.getYaw());
        if (location.getWorld() != null)
            result.add("world", jsonSerializationContext.serialize(location.getWorld().getUID(), UUID.class));
        return result;
    };
    private static final JsonDeserializer<Location> LOCATION_DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float pitch = jsonObject.get("pitch").getAsFloat();
        float yaw = jsonObject.get("yaw").getAsFloat();
        World world = jsonObject.has("world") ? jsonDeserializationContext.deserialize(jsonObject.get("world"),
                                                                                       UUID.class) : null;
        return new Location(world, x, y, z, yaw, pitch);
    };
    private static final JsonSerializer<ItemStack> ITEM_STACK_SERIALIZER = (itemStack, type, jsonSerializationContext) -> jsonSerializationContext.serialize(
            itemStack.serialize(),
            Map.class);
    private static final JsonDeserializer<ItemStack> ITEM_STACK_DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> ItemStack.deserialize(
            jsonDeserializationContext.deserialize(jsonElement, Map.class));

    public static GsonBuilder bukkitCompact() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Location.class, LOCATION_SERIALIZER);
        builder.registerTypeAdapter(Location.class, LOCATION_DESERIALIZER);
        builder.registerTypeAdapter(ItemStack.class, ITEM_STACK_SERIALIZER);
        builder.registerTypeAdapter(ItemStack.class, ITEM_STACK_DESERIALIZER);
        return builder;
    }
}
