package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("nms/Entity")
public class NMSEntity {
    public NMSEntity(Object o) {
    }

    @Shadow("nms/Entity.isInFluid")
    public boolean isInFluid(Tag tag) {
        return false;
    }
}
