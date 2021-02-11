package com.wheelcore.world;

import com.wheelcore.interfaces.block.BlockBase;
import com.wheelcore.manager.MainManager;
import com.wheelcore.utils.BlockUtil;
import com.wheelcore.utils.DebuggingLogger;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class WorldGenMinable {
    private final BlockBase ore;
    private final int numberOfBlocks;

    public WorldGenMinable(BlockBase ore, int numberOfBlocks) {
        this.ore = ore;
        this.numberOfBlocks = numberOfBlocks;
    }

    public boolean generate(World worldIn, Random rand, Chunk chunk, Location blockLoc) {
        int x = blockLoc.getBlockX();
        int y = blockLoc.getBlockY();
        int z = blockLoc.getBlockZ();

        Block currentBlock = chunk.getBlock(x, y, z);

        if (BlockUtil.isReplaceableOreGen(currentBlock)) {
            currentBlock.setType(this.ore.getMaterial());
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
            return false;
        }

        for (int i = 0; i < numberOfBlocks; i++) {  // Number of ore blocks in each ore
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
                currentBlock.setType(this.ore.getMaterial());
                MainManager.addBlock(currentBlock.getLocation(), this.ore, null);
            }
        }
        return true;
    }
}
