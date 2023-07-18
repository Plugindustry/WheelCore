package io.github.plugindustry.wheelcore.interfaces.entity;

import io.github.plugindustry.wheelcore.interfaces.Base;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;

public interface EntityBase extends Base {
    void onSpawn(@Nonnull Entity entity);

    void onLoad(@Nonnull Entity entity);

    void onDeath(@Nonnull Entity entity);

    void onUnload(@Nonnull Entity entity);

    @Nonnull
    EntityType getEntityType();
}