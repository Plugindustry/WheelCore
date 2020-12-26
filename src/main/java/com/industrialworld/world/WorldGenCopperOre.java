package com.industrialworld.world;

import com.industrialworld.block.CopperOre;
import com.industrialworld.interfaces.world.WorldGenerator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public class WorldGenCopperOre implements WorldGenerator {
    private final WorldGenMinable copperOreGenerator = new WorldGenMinable(new CopperOre(), 10);

    public boolean generate(World world, Random rand, Chunk chunk) {
        for (int i = 0, j = rand.nextInt(5) + 5; i <= j; ++i)
            copperOreGenerator.generate(world,
                                        rand,
                                        chunk,
                                        new Location(world, rand.nextInt(16), 10 + rand.nextInt(60), rand.nextInt(16)));
        return true;
    }
}
