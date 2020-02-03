package com.IndustrialWorld.world;

import com.IndustrialWorld.blocks.CopperOre;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public class WorldGenCopperOre extends WorldGenerator {
        private final WorldGenMinable copperOreGenerator = new WorldGenMinable(new CopperOre(), 10);

        @Override
        public boolean generate(World world, Random rand, Location lctn) {
            for (int i = 0; i < 10; ++i) { // Number of ore in each chunk
                int posX = (int) (lctn.getX() + rand.nextInt(16));
                int posY = 10 + rand.nextInt(60);
                int posZ = (int) (lctn.getZ() + rand.nextInt(16));
                Location blockPos = new Location(world, posX, posY, posZ);
                if (50 > rand.nextInt(100)) {
                    copperOreGenerator.generate(world, rand, blockPos);
                }
            }
            return true;
        }
}
