package com.czm.IndustrialWorld.interfaces;

import com.czm.IndustrialWorld.event.ProcessInfo;
import com.czm.IndustrialWorld.manager.BlockManager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public abstract class BlockBase {
    public void onPlace(BlockPlaceEvent event, ProcessInfo info) {
        if (!event.isCancelled())
            BlockManager.addBlock(BlockManager.getIdFromInstance(this), event.getBlock(), (BlockData) info.data);
    }

    public void onBreak(BlockBreakEvent event) {
        if (!event.isCancelled())
            BlockManager.removeBlock(event.getBlock());
    }
}
