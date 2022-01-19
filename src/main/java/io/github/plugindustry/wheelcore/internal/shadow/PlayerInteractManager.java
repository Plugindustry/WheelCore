package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("nms/PlayerInteractManager")
public class PlayerInteractManager {
    public PlayerInteractManager(Object o) {}

    @Shadow("nms/PlayerInteractManager.breakBlock")
    public boolean breakBlock(BlockPosition pos) {
        return false;
    }
}
