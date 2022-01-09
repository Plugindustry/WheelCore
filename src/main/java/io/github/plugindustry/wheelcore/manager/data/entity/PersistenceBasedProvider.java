package io.github.plugindustry.wheelcore.manager.data.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.plugindustry.wheelcore.WheelCore;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityBase;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityData;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.GsonHelper;
import io.github.plugindustry.wheelcore.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PersistenceBasedProvider implements EntityDataProvider {
    private final static NamespacedKey ENTITY_DATA_KEY = new NamespacedKey(WheelCore.instance, "entity_data");
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
        String data = entity.getPersistentDataContainer().get(ENTITY_DATA_KEY, PersistentDataType.STRING);
        if (data == null)
            return;
        EntityDescription description = gson.fromJson(data, EntityDescription.class);
        if (MainManager.getEntityMapping().containsKey(description.id))
            entityData.put(entity.getUniqueId(),
                           Pair.of(MainManager.getEntityMapping().get(description.id), description.data));
    }

    @Override
    public void unloadEntity(@Nonnull Entity entity) {
        UUID uuid = entity.getUniqueId();
        if (!entityData.containsKey(uuid))
            return;
        Pair<EntityBase, EntityData> pair = entityData.get(uuid);
        entity.getPersistentDataContainer().set(ENTITY_DATA_KEY,
                                                PersistentDataType.STRING,
                                                gson.toJson(new EntityDescription(MainManager.getEntityMapping()
                                                                                          .inverse()
                                                                                          .get(pair.first),
                                                                                  pair.second)));
        entityData.remove(uuid);
    }

    @Override
    public void beforeSave() {
        entityData.keySet().removeIf(uuid -> Bukkit.getEntity(uuid) == null);
        entityData.forEach((uuid, data) -> Objects.requireNonNull(Bukkit.getEntity(uuid))
                .getPersistentDataContainer()
                .set(ENTITY_DATA_KEY,
                     PersistentDataType.STRING,
                     gson.toJson(new EntityDescription(MainManager.getIdFromInstance(data.first), data.second))));
    }

    @Override
    public void afterSave() {}

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

        EntityDescription() {}

        EntityDescription(String id, EntityData data) {
            this.id = id;
            this.data = data;
        }
    }
}
