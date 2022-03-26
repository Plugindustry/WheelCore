package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("cb/CraftPlayer")
public class CraftPlayer {
    public CraftPlayer(Object o) {
    }

    @Shadow("cb/CraftPlayer.getHandle")
    public EntityPlayer getHandle() {
        return null;
    }
}
