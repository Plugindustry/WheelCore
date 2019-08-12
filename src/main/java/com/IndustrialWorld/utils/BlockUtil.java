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
        return new ArrayList<SearchResult>();
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
