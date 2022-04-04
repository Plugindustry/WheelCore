package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("cb/CraftBlock")
@SuppressWarnings("all")
public class CraftBlock {
    public CraftBlock(Object o) {
    }

    @Shadow("cb/CraftBlock.getHandle")
    public IBlockData getHandle() {
        return null;
    }
}