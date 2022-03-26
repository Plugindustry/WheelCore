package io.github.plugindustry.wheelcore.internal.shadow;

import io.github.czm23333.transparentreflect.annotations.Shadow;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@Shadow("cb/CraftItemStack")
public class CraftItemStack {
    public CraftItemStack(Object o) {
    }

    @Shadow("cb/CraftItemStack.asNMSCopy")
    @Nonnull
    public static NMSItemStack asNMSCopy(ItemStack itemStack) {
        return null;
    }

    @Shadow("cb/CraftItemStack.asCraftCopy")
    @Nonnull
    public static CraftItemStack asCraftCopy(ItemStack original) {
        return null;
    }
}
