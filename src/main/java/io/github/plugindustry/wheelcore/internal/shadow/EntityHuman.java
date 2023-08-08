package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("nms/EntityHuman")
@SuppressWarnings("all")
public class EntityHuman extends NMSEntity {
    public EntityHuman(Object o) {super(o);}

    @Shadow("nms/EntityHuman.getInventory")
    public PlayerInventory getInventory() {
        return null;
    }
}