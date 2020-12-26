package com.industrialworld.world;

import com.industrialworld.interfaces.world.WorldGenerator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.LinkedList;
import java.util.Random;

public class NormalOrePopulator extends BlockPopulator {
    public final static LinkedList<WorldGenerator> generators = new LinkedList<>();

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        generators.forEach(gen -> gen.generate(world, random, chunk));
    }
}
