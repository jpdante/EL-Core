package com.ellisiumx.elrankup.drop;

import com.ellisiumx.elrankup.crate.CrateManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DropManager implements Listener {

    public static DropManager context;

    public DropManager(JavaPlugin plugin) {
        context = this;
    }

    @EventHandler(ignoreCancelled = true)
    public void OnBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if(block.getType() != Material.EMERALD_BLOCK) return;
        event.setExpToDrop(0);
        event.getBlock().getDrops().clear();
    }

}
