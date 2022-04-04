package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("nms/Block")
@SuppressWarnings("all")
public class NMSBlock {
    public NMSBlock(Object o) {
    }

    @Shadow("nms/Block.getDataId")
    public static int getDataId(IBlockData data) {
        return 0;
    }

    @Shadow("nms/Block.getBlockData")
    public IBlockData getBlockData() {
        return null;
    }
}