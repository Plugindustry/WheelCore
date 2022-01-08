package io.github.plugindustry.wheelcore.interfaces.entity;

import io.github.plugindustry.wheelcore.interfaces.Base;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;

public interface EntityBase extends Base {
    void onSpawn();

    void onDespawn();

    @Nonnull
    EntityType getEntityType();
}
