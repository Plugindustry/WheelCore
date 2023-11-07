package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.DisabledConstructor;
import io.github.czm23333.transparentreflect.annotations.ShadowExtend;
import io.github.czm23333.transparentreflect.annotations.ShadowOverride;

@ShadowExtend("nms/Block")
public class MyNMSBlock {
    public float blastResistance = 0.0f;

    @DisabledConstructor
    public MyNMSBlock() {}

    @ShadowOverride("nms/Block.getBlastResistance")
    public float getBlastResistance() {
        return blastResistance;
    }
}