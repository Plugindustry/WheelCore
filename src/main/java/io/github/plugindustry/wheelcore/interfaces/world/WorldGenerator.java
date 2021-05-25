package io.github.plugindustry.wheelcore.interfaces.world;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Random;

public interface WorldGenerator {
    void generate(World world, Random rand, Chunk chunk);
}
