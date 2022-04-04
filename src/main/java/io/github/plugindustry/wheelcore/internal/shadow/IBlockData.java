package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("nms/IBlockData")
@SuppressWarnings("all")
public class IBlockData extends NMSBlockData {
    public IBlockData(Object o) {
        super(o);
    }

    @Shadow("nms/IBlockData.needCorrectTool")
    public boolean needCorrectTool() {
        return false;
    }
}