package io.github.plugindustry.wheelcore.world;

import io.github.plugindustry.wheelcore.interfaces.block.BlockBase;
import io.github.plugindustry.wheelcore.interfaces.world.WorldGenerator;
import io.github.plugindustry.wheelcore.manager.MainManager;
import io.github.plugindustry.wheelcore.utils.BlockUtil;
import io.github.plugindustry.wheelcore.utils.DebuggingLogger;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import java.util.Random;

public class WorldGenMinable implements WorldGenerator {
    private final BlockBase ore;
    private final int numberOfClusters;
    private final int numberOfBlocks;
    private final int minHeight;
    private final int maxHeight;

    public WorldGenMinable(BlockBase ore, int numberOfClusters, int numberOfBlocks, int minHeight, int maxHeight) {
        this.ore = ore;
        this.numberOfBlocks = numberOfBlocks;
        this.numberOfClusters = numberOfClusters;
        if (minHeight > maxHeight)
            throw new IllegalArgumentException("maxHeight can't be less than minHeight!");
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public void generate(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull Chunk chunk) {
        int clustersGen = 0;
        int triesGen = 0;
        for (; clustersGen < numberOfClusters && triesGen < numberOfClusters * 2; ++triesGen) {
            int x = rand.nextInt(16);
            int y = minHeight + rand.nextInt(maxHeight - minHeight + 1);
            int z = rand.nextInt(16);

            Block currentBlock = chunk.getBlock(x, y, z);

            if (BlockUtil.isReplaceableOreGen(currentBlock)) {
                ++clustersGen;
                currentBlock.setType(this.ore.getMaterial(), false);
                MainManager.addBlock(currentBlock.getLocation(), this.ore, null);
                DebuggingLogger.debug("Gen in " +
                        (currentBlock.getLocation().getWorld() == null ?
                                null :
                                currentBlock.getLocation().getWorld().getName()) +
                        " at " +
                        (chunk.getX() << 4) +
                        ", " +
                        (chunk.getZ() << 4));
            } else {
                continue;
            }

            for (int i = 1; i < numberOfBlocks; i++) {  // Number of ore blocks in each ore
                switch (rand.nextInt(6)) {
                    case 0:
                        x++;
                        break;
                    case 1:
                        y++;
                        break;
                    case 2:
                        z++;
                        break;
                    case 3:
                        x--;
                        break;
                    case 4:
                        y = Math.max(y - 1, 5);
                        break;
                    default:
                        z--;
                        break;
                }

                if (x < 1 || z < 1 || x >= 15 || z >= 15)
                    continue;
                currentBlock = chunk.getBlock(x, y, z);

                if (BlockUtil.isReplaceableOreGen(currentBlock)) {
                    currentBlock.setType(this.ore.getMaterial(), false);
                    MainManager.addBlock(currentBlock.getLocation(), this.ore, null);
                }
            }
        }
    }
}
