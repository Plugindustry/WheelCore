package io.github.plugindustry.wheelcore.world;

import io.github.plugindustry.wheelcore.interfaces.world.WorldGenerator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Random;

public class OverworldPopulator extends BlockPopulator {
    public final static LinkedList<WorldGenerator> generators = new LinkedList<>();

    @Override
    public void populate(@Nonnull World world, @Nonnull Random random, @Nonnull Chunk chunk) {
        generators.forEach(gen -> gen.generate(world, random, chunk));
    }
}
