package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;
import io.github.czm23333.transparentreflect.annotations.ShadowGetter;

@Shadow("nms/EntityPlayer")
public class EntityPlayer {
    public EntityPlayer(Object o) {}

    @ShadowGetter("nms/EntityPlayer.interactManager")
    public PlayerInteractManager getInteractManager() {
        return null;
    }
}
