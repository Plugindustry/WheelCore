package com.IndustrialWorld.interfaces;

import com.IndustrialWorld.event.ProcessInfo;
import com.IndustrialWorld.manager.MainManager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class BlockBase extends Base {
    public void onPlace(BlockPlaceEvent event, ProcessInfo info) {
        if (!event.isCancelled())
            MainManager.addBlock(MainManager.getIdFromInstance(this), event.getBlock(), (BlockData) info.data);
    }

    public void onBreak(BlockBreakEvent event) {
        if (!event.isCancelled())
            MainManager.removeBlock(event.getBlock());
    }

    public void onInteractAsBlock(PlayerInteractEvent event){

    }
}
