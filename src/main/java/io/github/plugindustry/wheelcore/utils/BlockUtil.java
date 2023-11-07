package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.Destroyable;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.interfaces.item.Tool;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.Packet;
import io.github.plugindustry.wheelcore.interfaces.transport.packet.PacketConsumer;
import io.github.plugindustry.wheelcore.internal.shadow.CraftBlock;
import io.github.plugindustry.wheelcore.internal.shadow.CraftItemStack;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BlockUtil {
    private static final List<Material> replaceableOreGenList = Arrays.asList(Material.STONE, Material.GRANITE,
            Material.DIORITE, Material.ANDESITE, Material.NETHERRACK, Material.END_STONE);

    public static Stream<Location> findAcceptableAround(@Nonnull Location src, @Nullable Packet packet) {
        return Stream.of(src.clone().add(1, 0, 0), src.clone().add(-1, 0, 0), src.clone().add(0, 1, 0),
                        src.clone().add(0, -1, 0), src.clone().add(0, 0, 1), src.clone().add(0, 0, -1))
                .filter(loc -> isAcceptable(loc, packet));

    }

    @SuppressWarnings("rawtypes")
    public static boolean isAcceptable(@Nonnull Location block, @Nullable Packet packet) {
        return MainManager.hasBlock(block) && MainManager.getBlockInstance(block) instanceof PacketConsumer pc &&
               pc.canAccept(packet) && pc.available(block);
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

    public static Optional<Float> getOverwrittenBlastResistance(@Nonnull Block block) {
        BlockBase instance = MainManager.getBlockInstance(block.getLocation());
        if (instance == null) return Optional.empty();
        if (!(instance instanceof Destroyable)) return Optional.empty();
        return ((Destroyable) instance).getBlastResistance(block);
    }
}