package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("cb/CraftPlayer")
@SuppressWarnings("all")
public class CraftPlayer extends CraftHumanEntity {
    public CraftPlayer(Object o) {
        super(o);
    }

    @Shadow("cb/CraftPlayer.getHandle")
    public EntityPlayer getHandle() {
        return null;
    }
}