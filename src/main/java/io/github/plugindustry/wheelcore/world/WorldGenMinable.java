package io.github.plugindustry.wheelcore.world;

import io.github.plugindustry.wheelcore.interfaces.block.Placeable;
import io.github.plugindustry.wheelcore.interfaces.world.WorldGenerator;
import io.github.plugindustry.wheelcore.utils.BlockUtil;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public final class WorldGenMinable implements WorldGenerator {
    private final Placeable ore;
    private final int numberOfClusters;
    private final int numberOfBlocks;
    private final int minHeight;
    private final int maxHeight;
    private final Function<Block, Boolean> verifier;

    public WorldGenMinable(@Nonnull Placeable ore, int numberOfClusters, int numberOfBlocks, int minHeight,
            int maxHeight, Function<Block, Boolean> verifier) {
        this.ore = Objects.requireNonNull(ore);
        this.numberOfBlocks = numberOfBlocks;
        this.numberOfClusters = numberOfClusters;
        if (minHeight > maxHeight) throw new IllegalArgumentException("maxHeight can't be less than minHeight!");
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.verifier = verifier;
    }

    public WorldGenMinable(Placeable ore, int numberOfClusters, int numberOfBlocks, int minHeight, int maxHeight) {
        this(ore, numberOfClusters, numberOfBlocks, minHeight, maxHeight, BlockUtil::isReplaceableOreGen);
    }

    public void generate(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull Chunk chunk) {
        for (int clustersGen = 0, triesGen = 0; clustersGen < numberOfClusters && triesGen < numberOfClusters * 2;
                ++triesGen) {
            int x = rand.nextInt(14) + 1; // Prevent generating on border blocks, which may cause infinite chunk loading
            int y = minHeight + rand.nextInt(maxHeight - minHeight + 1);
            int z = rand.nextInt(14) + 1;

            if (verifier.apply(chunk.getBlock(x, y, z))) ++clustersGen;
            else continue;

            int yLen = (int) (rand.nextBoolean() ? Math.ceil(Math.cbrt(numberOfBlocks)) :
                    Math.floor(Math.cbrt(numberOfBlocks)));
            int zLen = (int) (rand.nextBoolean() ? Math.ceil(Math.cbrt(numberOfBlocks)) :
                    Math.floor(Math.cbrt(numberOfBlocks)));
            int blocksGen = 0;
            for (int dx = 0; dx + x < 15 && blocksGen < numberOfBlocks; ++dx)
                for (int dy = 0; dy < yLen && dy + y <= maxHeight && blocksGen < numberOfBlocks; ++dy)
                    for (int dz = 0; dz < zLen && dz + z < 15 && blocksGen < numberOfBlocks; ++dz) {
                        Block block = chunk.getBlock(x + dx, y + dy, z + dz);
                        if (verifier.apply(block)) {
                            ore.onBlockPlace(null, block, null, null);
                            ++blocksGen;
                        }
                    }
        }
    }
}