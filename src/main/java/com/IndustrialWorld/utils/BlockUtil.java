package com.IndustrialWorld.utils;

import com.IndustrialWorld.interfaces.MachineBlock;
import com.IndustrialWorld.interfaces.Wire;
import com.IndustrialWorld.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

public class BlockUtil {

    public List<SearchResult> searchFromWire(Block wireBlock) {
        List<SearchResult> result = new ArrayList<>();
        World wW = wireBlock.getWorld(); // Wire's world
        int wX = wireBlock.getX(); // Wire's X pos
        int wY = wireBlock.getY();
        int wZ = wireBlock.getZ();

        int cB = 0; // Current branch number
        int step = 0; // Current step
        int tX,tY,tZ;
        List<Location> lctn2BChecked = Arrays.asList(
                new Location(wW, wX+1, wY, wZ),
                new Location(wW, wX-1, wY, wZ),
                new Location(wW, wX, wY+1, wZ),
                new Location(wW, wX, wY-1, wZ),
                new Location(wW, wX, wY, wZ+1),
                new Location(wW, wX, wY, wZ-1)); // Locations should be checked
        List<Location> checkedLctn = new ArrayList<>();

        while (!lctn2BChecked.isEmpty()) {
            for (Location tmpLctn : lctn2BChecked) {
                if (!checkedLctn.contains(tmpLctn)) {
                    if (isWire(tmpLctn.getBlock())) {
                        checkedLctn.add(tmpLctn);

                        tX = tmpLctn.getBlockX(); tY= tmpLctn.getBlockY(); tZ = tmpLctn.getBlockZ();

                        Collections.addAll(lctn2BChecked,
                                new Location(wW, tX+1, tY, tZ),
                                new Location(wW, tX-1, tY, tZ),
                                new Location(wW, tX, tY+1, tZ),
                                new Location(wW, tX, tY-1, tZ),
                                new Location(wW, tX, tY, tZ+1),
                                new Location(wW, tX, tY, tZ-1));

                    } else if (isMachine(tmpLctn.getBlock())) {
                        result.add(new SearchResult(tmpLctn.getBlock(), step, cB));
                    }
                }
            }
        }

        return result;
    }

    public class SearchResult {
        Block mBlock;
        int numWire;
        int numBranch;

        public SearchResult(Block mBlock, int numWire, int numBranch) {
            this.mBlock = mBlock;
            this.numWire = numWire;
            this.numBranch = numBranch;
        }
    }

    public static boolean isMachine(Block block) { return MainManager.hasBlock(block) ? MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof MachineBlock : false; }
    public static boolean isWire(Block block) { return MainManager.hasBlock(block) ? MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof Wire : false; }
}
