package io.github.plugindustry.wheelcore.interfaces.world;

import org.bukkit.Chunk;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.util.Random;

public interface WorldGenerator {
    void generate(@Nonnull World world, @Nonnull Random rand, @Nonnull Chunk chunk);
}
