package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("cb/CraftHumanEntity")
@SuppressWarnings("all")
public class CraftHumanEntity {
    public CraftHumanEntity(Object o) {}

    @Shadow("cb/CraftHumanEntity.getHandle")
    public EntityHuman getHandle() {
        return null;
    }
}