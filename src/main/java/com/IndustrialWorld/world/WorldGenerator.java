package com.IndustrialWorld.world;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public abstract class WorldGenerator {
    public boolean generate(World world, Random rand, Location lctn) {
        return false;
    }
}
