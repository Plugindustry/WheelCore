package io.github.plugindustry.wheelcore.manager.data.entity;

import io.github.plugindustry.wheelcore.interfaces.entity.EntityBase;
import io.github.plugindustry.wheelcore.interfaces.entity.EntityData;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EntityDataProvider {
    @Nonnull
    // TODO: Provider switching according to the config
    static EntityDataProvider defaultProvider() {
        return new PersistenceBasedProvider();
    }

    void loadEntity(@Nonnull Entity entity);

    void unloadEntity(@Nonnull Entity entity);

    void beforeSave();

    void afterSave();

    @Nullable
    EntityBase instanceOf(@Nonnull Entity entity);

    @Nullable
    EntityData getData(@Nonnull Entity entity);

    void setData(@Nonnull Entity entity, @Nullable EntityData data);
}
