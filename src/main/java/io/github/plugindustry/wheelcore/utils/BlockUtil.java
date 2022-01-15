package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.block.Destroyable;
import io.github.plugindustry.wheelcore.interfaces.block.Wire;
import io.github.plugindustry.wheelcore.interfaces.item.ItemBase;
import io.github.plugindustry.wheelcore.interfaces.item.Tool;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BlockUtil {
    private static final List<Material> replaceableOreGenList = Arrays.asList(Material.STONE,
                                                                              Material.GRANITE,
                                                                              Material.DIORITE,
                                                                              Material.ANDESITE,
                                                                              Material.NETHERRACK,
                                                                              Material.END_STONE);
    private static final Map<Material, Float> commonToolBonus = new EnumMap<>(Material.class);

    static {
        commonToolBonus.put(Material.WOODEN_AXE, 2.0f);
        commonToolBonus.put(Material.WOODEN_PICKAXE, 2.0f);
        commonToolBonus.put(Material.WOODEN_SHOVEL, 2.0f);
        commonToolBonus.put(Material.STONE_AXE, 4.0f);
        commonToolBonus.put(Material.STONE_PICKAXE, 4.0f);
        commonToolBonus.put(Material.STONE_SHOVEL, 4.0f);
        commonToolBonus.put(Material.IRON_AXE, 6.0f);
        commonToolBonus.put(Material.IRON_PICKAXE, 6.0f);
        commonToolBonus.put(Material.IRON_SHOVEL, 6.0f);
        commonToolBonus.put(Material.DIAMOND_AXE, 8.0f);
        commonToolBonus.put(Material.DIAMOND_PICKAXE, 8.0f);
        commonToolBonus.put(Material.DIAMOND_SHOVEL, 8.0f);
        commonToolBonus.put(Material.GOLDEN_AXE, 12.0f);
        commonToolBonus.put(Material.GOLDEN_PICKAXE, 12.0f);
        commonToolBonus.put(Material.GOLDEN_SHOVEL, 12.0f);
        if (Material.getMaterial("NETHERITE_AXE") != null) {
            commonToolBonus.put(Material.NETHERITE_AXE, 9.0f);
            commonToolBonus.put(Material.NETHERITE_PICKAXE, 9.0f);
            commonToolBonus.put(Material.NETHERITE_SHOVEL, 9.0f);
        }
    }

    public static Stream<Location> findWireAround(@Nonnull Location src) {
        return Stream.of(src.clone().add(1, 0, 0),
                         src.clone().add(-1, 0, 0),
                         src.clone().add(0, 1, 0),
                         src.clone().add(0, -1, 0),
                         src.clone().add(0, 0, 1),
                         src.clone().add(0, 0, -1)).filter(BlockUtil::isWire);
    }

    public static boolean isWire(@Nonnull Location block) {
        return MainManager.hasBlock(block) && MainManager.getBlockInstance(block) instanceof Wire;
    }

    public static boolean isReplaceableOreGen(@Nonnull Block block) {
        return !MainManager.hasBlock(block.getLocation()) && replaceableOreGenList.contains(block.getType());
    }

    // TODO: Compact API
    public static float getVanillaHardness(@Nonnull Block block) {
        return block.getType().getHardness();
    }

    // TODO: Compact API
    public static boolean isVanillaPreferredTool(@Nonnull Block block, @Nonnull ItemStack tool) {
        return block.isPreferredTool(tool);
    }

    public static float getHardness(@Nonnull Block block) {
        BlockBase instance = MainManager.getBlockInstance(block.getLocation());
        if (instance == null)
            return getVanillaHardness(block);
        if (!(instance instanceof Destroyable))
            return -1;
        return ((Destroyable) instance).getHardness(block);
    }

    public static boolean isPreferredTool(@Nonnull Block block, @Nonnull ItemStack tool) {
        ItemBase itemInstance = MainManager.getItemInstance(tool);
        if (itemInstance instanceof Tool)
            if (((Tool) itemInstance).isSuitable(block, tool))
                return true;

        BlockBase instance = MainManager.getBlockInstance(block.getLocation());
        if (instance == null)
            return itemInstance == null && isVanillaPreferredTool(block, tool);
        if (!(instance instanceof Destroyable))
            return false;
        return ((Destroyable) instance).isPreferredTool(block, tool);
    }

    public static float getVanillaToolBonus(@Nonnull Block block, @Nonnull ItemStack tool) {
        Material blockType = block.getType();
        Material itemType = tool.getType();
        if (commonToolBonus.containsKey(itemType))
            return commonToolBonus.get(itemType);
        if (itemType == Material.SHEARS) {
            if (blockType.name().endsWith("WOOL"))
                return 5;
            else if (blockType == Material.COBWEB)
                return 15;
            else
                return 1.5f;
        } else if (itemType.name().endsWith("SWORD")) {
            if (blockType == Material.COBWEB)
                return 15;
            else
                return 1.5f;
        }
        return 1;
    }

    public static float getToolBonus(@Nonnull Block block, @Nonnull ItemStack tool) {
        ItemBase instance = MainManager.getItemInstance(tool);
        if (instance == null)
            return getVanillaToolBonus(block, tool);
        if (!(instance instanceof Tool))
            return 1;
        return ((Tool) instance).getToolBonus(block, tool);
    }

}
