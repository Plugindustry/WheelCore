package com.industrialworld.world;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class NormalOrePopulator extends BlockPopulator {
    static WorldGenerator copperGen = new WorldGenCopperOre();

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        copperGen.generate(world, random, chunk);
    }
}
