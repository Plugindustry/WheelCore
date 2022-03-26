package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;

@Shadow("nms/ItemStack")
public class NMSItemStack {
    public NMSItemStack(Object o) {
    }

    @Shadow("nms/ItemStack.isPreferredTool")
    public boolean isPreferredTool(IBlockData block) {
        return false;
    }

    @Shadow("nms/ItemStack.getToolBonus")
    public float getToolBonus(IBlockData block) {
        return 1.0f;
    }

    @Shadow("nms/ItemStack.save")
    public NBTTagCompound save(NBTTagCompound tag) {
        return null;
    }
}
