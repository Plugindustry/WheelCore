package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("nms/PlayerInventory")
@SuppressWarnings("all")
public class PlayerInventory {
    public static final int[] EQUIPMENT_SLOTS = new int[]{0, 1, 2, 3};

    public PlayerInventory(Object o) {}

    @Shadow("nms/PlayerInventory.costDurability")
    public void costDurability(DamageSource damageSource, float damage, int[] slots) {}
}