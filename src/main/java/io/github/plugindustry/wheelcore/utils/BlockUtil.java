package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.Destroyable;
import io.github.plugindustry.wheelcore.interfaces.block.Wire;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.interfaces.item.Tool;
import io.github.plugindustry.wheelcore.internal.shadow.CraftBlock;
import io.github.plugindustry.wheelcore.internal.shadow.CraftItemStack;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BlockUtil {
    private static final List<Material> replaceableOreGenList =
            Arrays.asList(Material.STONE, Material.GRANITE, Material.DIORITE, Material.ANDESITE,
                    Material.NETHERRACK, Material.END_STONE);

    public static Stream<Location> findWireAround(@Nonnull Location src) {
        return Stream.of(src.clone().add(1, 0, 0), src.clone().add(-1, 0, 0), src.clone().add(0, 1, 0),
                             src.clone().add(0, -1, 0), src.clone().add(0, 0, 1), src.clone().add(0, 0, -1))
                     .filter(BlockUtil::isWire);
    }

    public static boolean isWire(@Nonnull Location block) {
        return MainManager.hasBlock(block) && MainManager.getBlockInstance(block) instanceof Wire;
    }

    public static boolean isReplaceableOreGen(@Nonnull Block block) {
        return !MainManager.hasBlock(block.getLocation()) && replaceableOreGenList.contains(block.getType());
    }

    public static float getVanillaHardness(@Nonnull Block block) {
        return new CraftBlock(block).getHandle().getHardness();
    }

    public static boolean isVanillaPreferredTool(@Nonnull Block block, @Nonnull ItemStack tool) {
        return CraftItemStack.asNMSCopy(tool).isPreferredTool(new CraftBlock(block).getHandle());
    }

    public static float getHardness(@Nonnull Block block) {
        BlockBase instance = MainManager.getBlockInstance(block.getLocation());
        if (instance == null) return getVanillaHardness(block);
        if (!(instance instanceof Destroyable)) return -1;
        return ((Destroyable) instance).getHardness(block);
    }

    public static boolean isPreferredTool(@Nonnull Block block, @Nonnull ItemStack tool) {
        ItemBase itemInstance = MainManager.getItemInstance(tool);
        if (itemInstance instanceof Tool) if (((Tool) itemInstance).isSuitable(block, tool)) return true;

        BlockBase instance = MainManager.getBlockInstance(block.getLocation());
        if (instance == null) return itemInstance == null && isVanillaPreferredTool(block, tool);
        if (!(instance instanceof Destroyable)) return false;
        return ((Destroyable) instance).isPreferredTool(block, tool);
    }

    public static float getVanillaToolBonus(@Nonnull Block block, @Nonnull ItemStack tool) {
        return CraftItemStack.asNMSCopy(tool).getToolBonus(new CraftBlock(block).getHandle());
    }

    public static float getToolBonus(@Nonnull Block block, @Nonnull ItemStack tool) {
        ItemBase instance = MainManager.getItemInstance(tool);
        if (instance == null) return getVanillaToolBonus(block, tool);
        if (!(instance instanceof Tool)) return 1;
        return ((Tool) instance).getToolBonus(block, tool);
    }

    public static boolean vanillaNeedCorrectTool(@Nonnull Block block) {
        return new CraftBlock(block).getHandle().needCorrectTool();
    }

    public static boolean needCorrectTool(@Nonnull Block block) {
        BlockBase instance = MainManager.getBlockInstance(block.getLocation());
        if (instance == null) return vanillaNeedCorrectTool(block);
        if (!(instance instanceof Destroyable)) return true;
        return ((Destroyable) instance).needCorrectTool(block);
    }
}
