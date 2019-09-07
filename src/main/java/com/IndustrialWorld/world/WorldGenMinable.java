package com.IndustrialWorld.world;

import com.IndustrialWorld.interfaces.OreBase;
import com.IndustrialWorld.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class WorldGenMinable extends WorldGenerator {
    private final OreBase ore;
    private final int numberOfBlocks;

    public WorldGenMinable(OreBase ore, int numberOfBlocks) {
        if (ore.isOre()) {
            this.ore = ore;
            this.numberOfBlocks = numberOfBlocks;
        } else throw new IllegalArgumentException();
    }

    public boolean generate(World worldIn, Random rand, Location blockLctn) {
        for (int i = 0; i < 10; ++i) {
            int x = blockLctn.getBlockX();
            int y = blockLctn.getBlockY();
            int z = blockLctn.getBlockZ();

            Block cB = new Location(worldIn, x, y, z).getBlock();

            cB.setType(this.ore.getMaterial());
            MainManager.addBlock(this.ore.getId(), cB, null);

            switch (rand.nextInt(6)) {
                case 0: x++; break;
                case 1: y++; break;
                case 2: z++; break;
                case 3: x--; break;
                case 4: y = Math.max(y - 1, 0); break;
                default: z--; break;
                }
        }
        return true;
    }
}
