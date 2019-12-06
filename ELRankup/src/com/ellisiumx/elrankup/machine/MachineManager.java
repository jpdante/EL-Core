package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.repository.MachineRepository;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class MachineManager implements Listener {

    private MachineRepository machineRepository;

    public MachineManager(JavaPlugin plugin) {
        machineRepository = new MachineRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if(block == null || block.getType() == Material.AIR) return;
        if(!UtilNBT.contains(block, "Machine")) return;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block == null || block.getType() == Material.AIR) return;
        if(!UtilNBT.contains(block, "Machine")) return;

    }

    public static ItemStack getFuel(int liters, int amount, int boost) {
        ItemStack itemStack = RankupConfiguration.Fuel.clone();
        itemStack.setAmount(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replaceAll("%liters%", String.valueOf(liters)));
        ArrayList<String> lore = new ArrayList<>();
        for(String loreItem : itemMeta.getLore()) {
            lore.add(loreItem.replaceAll("%liters%", String.valueOf(liters)));
        }
        itemMeta.setLore(lore);
        itemStack = UtilNBT.set(itemStack, true,"MachineFuel");
        itemStack = UtilNBT.set(itemStack, liters,"Liters");
        itemStack = UtilNBT.set(itemStack, boost,"Boost");
        return itemStack;
    }
}
