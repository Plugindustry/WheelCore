package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;
import io.github.czm23333.transparentreflect.annotations.ShadowGetter;

@Shadow("nms/BlockData")
@SuppressWarnings("all")
public class NMSBlockData {
    public NMSBlockData(Object o) {
    }

    @ShadowGetter("nms/BlockData.hardness")
    public float getHardness() {
        return 0.0f;
    }
}