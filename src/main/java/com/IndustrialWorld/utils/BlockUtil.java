package com.IndustrialWorld.utils;

import com.IndustrialWorld.interfaces.MachineBlock;
import com.IndustrialWorld.interfaces.Wire;
import com.IndustrialWorld.manager.MainManager;
import org.bukkit.block.Block;

public class BlockUtil {
    public static boolean isMachine(Block block) { return MainManager.hasBlock(block) ? MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof MachineBlock : false; }
    public static boolean isWire(Block block) { return MainManager.hasBlock(block) ? MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof Wire : false; }
}
