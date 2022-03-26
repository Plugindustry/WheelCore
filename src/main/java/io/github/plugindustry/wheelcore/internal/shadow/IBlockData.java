package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("nms/IBlockData")
public class IBlockData {
    public IBlockData(Object o) {
    }

    @Shadow("nms/IBlockData.needCorrectTool")
    public boolean needCorrectTool() {
        return false;
    }
}
