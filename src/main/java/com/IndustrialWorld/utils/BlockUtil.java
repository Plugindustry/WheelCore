package com.IndustrialWorld.utils;

import com.IndustrialWorld.interfaces.MachineBlock;
import com.IndustrialWorld.interfaces.Wire;
import com.IndustrialWorld.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockUtil {

    public boolean isIWBlock(Block block) { return MainManager.hasBlock(block); }
    public boolean isMachine(Block block) { return isIWBlock(block) ? MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof MachineBlock : false; }
    public boolean isWire(Block block) { return isIWBlock(block) ? MainManager.getInstanceFromId(MainManager.getBlockId(block)) instanceof Wire : false }
}
