package io.github.plugindustry.wheelcore.manager.data.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityBase;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityData;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.GsonHelper;
import io.github.plugindustry.wheelcore.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class TagBasedProvider implements EntityDataProvider {
    private static final Gson gson;

    static {
        GsonBuilder gbs = GsonHelper.bukkitCompact();
        gbs.registerTypeAdapter(EntityData.class, GsonHelper.POLYMORPHISM_SERIALIZER);
        gbs.registerTypeAdapter(EntityData.class, GsonHelper.POLYMORPHISM_DESERIALIZER);
        gson = gbs.create();
    }

    private final HashMap<UUID, Pair<EntityBase, EntityData>> entityData = new HashMap<>();

    @Override
    public void loadEntity(@Nonnull Entity entity) {
        entity.getScoreboardTags()
                .stream()
                .filter(str -> str.startsWith("<WheelCoreData>"))
                .findFirst()
                .ifPresent(tag -> {
                    entity.removeScoreboardTag(tag);
                    EntityDescription description = gson.fromJson(tag.substring("<WheelCoreData>".length()),
                            EntityDescription.class);
                    if (MainManager.getEntityMapping().containsKey(description.id))
                        entityData.put(entity.getUniqueId(),
                                Pair.of(MainManager.getEntityMapping().get(description.id), description.data));
                });
    }

    @Override
    public void unloadEntity(@Nonnull Entity entity) {
        UUID uuid = entity.getUniqueId();
        if (!entityData.containsKey(uuid))
            return;
        Pair<EntityBase, EntityData> pair = entityData.get(uuid);
        entity.addScoreboardTag("<WheelCoreData>" +
                gson.toJson(new EntityDescription(MainManager.getEntityMapping()
                        .inverse()
                        .get(pair.first), pair.second)));
        entityData.remove(uuid);
    }

    @Override
    public void beforeSave() {
        entityData.keySet().removeIf(uuid -> Bukkit.getEntity(uuid) == null);
        entityData.forEach((uuid, data) -> Objects.requireNonNull(Bukkit.getEntity(uuid))
                .addScoreboardTag("<WheelCoreData>" +
                        gson.toJson(new EntityDescription(MainManager.getIdFromInstance(data.first),
                                data.second))));
    }

    @Override
    public void afterSave() {
        entityData.keySet()
                .stream()
                .map(Bukkit::getEntity)
                .filter(Objects::nonNull)
                .forEach(entity -> entity.getScoreboardTags()
                        .stream()
                        .filter(str -> str.startsWith("<WheelCoreData>"))
                        .forEach(entity::removeScoreboardTag));
    }

    @Nullable
    @Override
    public EntityBase instanceOf(@Nonnull Entity entity) {
        return entityData.containsKey(entity.getUniqueId()) ? entityData.get(entity.getUniqueId()).first : null;
    }

    @Nullable
    @Override
    public EntityData getData(@Nonnull Entity entity) {
        return entityData.containsKey(entity.getUniqueId()) ? entityData.get(entity.getUniqueId()).second : null;
    }

    @Override
    public void setData(@Nonnull Entity entity, @Nullable EntityData data) {
        if (entityData.containsKey(entity.getUniqueId()))
            entityData.get(entity.getUniqueId()).second = data;
    }

    public static class EntityDescription {
        String id;
        EntityData data;

        EntityDescription() {
        }

        EntityDescription(String id, EntityData data) {
            this.id = id;
            this.data = data;
        }
    }
}
