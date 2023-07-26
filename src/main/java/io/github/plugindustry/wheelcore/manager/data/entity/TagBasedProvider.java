package io.github.plugindustry.wheelcore.manager.data.entity;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityBase;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityData;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.GsonHelper;
import io.github.plugindustry.wheelcore.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TagBasedProvider implements EntityDataProvider {
    public static final JsonSerializer<EntityDescription> ENTITY_DESC_SERIALIZER = (obj, type, jsonSerializationContext) -> {
        JsonObject result = new JsonObject();
        result.add("id", jsonSerializationContext.serialize(obj.id));
        result.add("data",
                jsonSerializationContext.serialize(obj.data, new TypeToken<Map<NamespacedKey, EntityData>>() {
                }.getType()));
        return result;
    };
    public static final JsonDeserializer<EntityDescription> ENTITY_DESC_DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        EntityDescription desc = new EntityDescription();
        desc.id = jsonDeserializationContext.deserialize(jsonObject.get("id"), NamespacedKey.class);
        desc.data = jsonDeserializationContext.deserialize(jsonObject.get("data"),
                new TypeToken<Map<NamespacedKey, EntityData>>() {
                }.getType());
        return desc;
    };
    private static final Gson gson;

    static {
        GsonBuilder gbs = GsonHelper.bukkitCompat();
        gbs.registerTypeAdapter(EntityData.class, GsonHelper.POLYMORPHISM_SERIALIZER);
        gbs.registerTypeAdapter(EntityData.class, GsonHelper.POLYMORPHISM_DESERIALIZER);
        gbs.registerTypeAdapter(EntityDescription.class, ENTITY_DESC_SERIALIZER);
        gbs.registerTypeAdapter(EntityDescription.class, ENTITY_DESC_DESERIALIZER);
        gson = gbs.create();
    }

    private final HashMap<UUID, Pair<EntityBase, Map<NamespacedKey, EntityData>>> entities = new HashMap<>();

    @Override
    public void loadEntity(@Nonnull Entity entity) {
        entity.getScoreboardTags().stream().filter(str -> str.startsWith("<WheelCoreData>")).findFirst()
                .ifPresent(tag -> {
                    entity.removeScoreboardTag(tag);
                    EntityDescription description = gson.fromJson(tag.substring("<WheelCoreData>".length()),
                            EntityDescription.class);
                    if (MainManager.getEntityMapping().containsKey(description.id)) entities.put(entity.getUniqueId(),
                            Pair.of(MainManager.getEntityMapping().get(description.id), description.data));
                });
    }

    @Override
    public void unloadEntity(@Nonnull Entity entity) {
        UUID uuid = entity.getUniqueId();
        if (!entities.containsKey(uuid)) return;
        Pair<EntityBase, Map<NamespacedKey, EntityData>> pair = entities.get(uuid);
        entity.addScoreboardTag("<WheelCoreData>" + gson.toJson(
                new EntityDescription(MainManager.getIdFromInstance(pair.first), pair.second)));
        entities.remove(uuid);
    }

    @Override
    public void beforeSave() {
        entities.keySet().removeIf(uuid -> Bukkit.getEntity(uuid) == null);
        entities.forEach((uuid, data) -> Objects.requireNonNull(Bukkit.getEntity(uuid)).addScoreboardTag(
                "<WheelCoreData>" +
                gson.toJson(new EntityDescription(MainManager.getIdFromInstance(data.first), data.second))));
    }

    @Override
    public void afterSave() {
        entities.keySet().stream().map(Bukkit::getEntity).filter(Objects::nonNull).forEach(
                entity -> entity.getScoreboardTags().stream().filter(str -> str.startsWith("<WheelCoreData>"))
                        .forEach(entity::removeScoreboardTag));
    }

    @Nullable
    @Override
    public EntityBase instanceOf(@Nonnull Entity entity) {
        return hasEntity(entity) ? entities.get(entity.getUniqueId()).first : null;
    }

    @Override
    public boolean hasEntity(@Nonnull Entity entity) {
        return entities.containsKey(entity.getUniqueId()) && entities.get(entity.getUniqueId()).first != null;
    }

    @Nullable
    @Override
    public EntityData getData(@Nonnull Entity entity) {
        return hasEntity(entity) ? entities.get(entity.getUniqueId()).second.get(null) : null;
    }

    @Override
    public void setData(@Nonnull Entity entity, @Nullable EntityData data) {
        if (hasEntity(entity)) entities.get(entity.getUniqueId()).second.put(null, data);
    }

    @Nullable
    @Override
    public EntityData getAdditionalData(@Nonnull Entity entity, @Nonnull NamespacedKey key) {
        Objects.requireNonNull(key);

        return entities.containsKey(entity.getUniqueId()) ? entities.get(entity.getUniqueId()).second.get(key) : null;
    }

    @Override
    public void setAdditionalData(@Nonnull Entity entity, @Nonnull NamespacedKey key, @Nullable EntityData data) {
        UUID uuid = entity.getUniqueId();
        if (!entities.containsKey(uuid)) entities.put(uuid, Pair.of(null, new HashMap<>()));

        Pair<EntityBase, Map<NamespacedKey, EntityData>> pair = entities.get(uuid);

        if (data == null) pair.second.remove(key);
        else pair.second.put(key, data);
    }

    @Override
    public void addEntity(@Nonnull Entity entity, @Nonnull EntityBase instance, @Nullable EntityData data) {
        UUID uuid = entity.getUniqueId();
        Map<NamespacedKey, EntityData> map = entities.containsKey(uuid) ? entities.get(uuid).second : new HashMap<>();
        if (!(data == null)) map.put(null, data);
        entities.put(uuid, Pair.of(instance, map));
    }

    private static class EntityDescription {
        public NamespacedKey id;
        public Map<NamespacedKey, EntityData> data;

        public EntityDescription() {
        }

        public EntityDescription(NamespacedKey id, Map<NamespacedKey, EntityData> data) {
            this.id = id;
            this.data = data;
        }
    }
}