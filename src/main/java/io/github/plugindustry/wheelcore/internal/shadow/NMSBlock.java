package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("nms/Block")
public class NMSBlock {
    public NMSBlock(Object o) {}

    @Shadow("nms/Block.getDataId")
    public static int getDataId(IBlockData data) {
        return 0;
    }
}