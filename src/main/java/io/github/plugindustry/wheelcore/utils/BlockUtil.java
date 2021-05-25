package io.github.plugindustry.wheelcore.utils;

import io.github.plugindustry.wheelcore.interfaces.block.Wire;
import io.github.plugindustry.wheelcore.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BlockUtil {
    private static final List<Material> replaceableOreGenList = Arrays.asList(Material.STONE,
                                                                              Material.GRANITE,
                                                                              Material.DIORITE,
                                                                              Material.ANDESITE,
                                                                              Material.NETHERRACK,
                                                                              Material.END_STONE);

    public static Stream<Location> findWireAround(Location src) {
        return Stream.of(src.clone().add(1, 0, 0),
                         src.clone().add(-1, 0, 0),
                         src.clone().add(0, 1, 0),
                         src.clone().add(0, -1, 0),
                         src.clone().add(0, 0, 1),
                         src.clone().add(0, 0, -1)).filter(BlockUtil::isWire);
    }

    public static boolean isWire(Location block) {
        return MainManager.hasBlock(block) && MainManager.getBlockInstance(block) instanceof Wire;
    }

    public static boolean isReplaceableOreGen(Block block) {
        return !MainManager.hasBlock(block.getLocation()) && replaceableOreGenList.contains(block.getType());
    }
}
