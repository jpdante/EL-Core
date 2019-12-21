package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.timing.TimingManager;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.command.TestBuyMachine;
import com.ellisiumx.elrankup.machine.repository.MachineRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

public class MachineManager implements Listener {

    private static MachineManager context;
    private boolean initialized;
    private MachineRepository repository;
    private HashMap<Location, Machine> machines;

    public MachineManager(JavaPlugin plugin) {
        context = this;
        repository = new MachineRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initialized = false;
        new TestBuyMachine(plugin);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start("load machines");
            machines = repository.getMachines();
            TimingManager.stop("load machines");
            UtilLog.log(Level.INFO, "[Machines] " + machines.size() + " machines loaded from mysql.");
            initialized = true;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if(!initialized) return;
        Block block = event.getBlock();
        if(block == null || block.getType() == Material.AIR) return;
        if(!machines.containsKey(event.getBlock().getLocation())) return;
        event.setCancelled(true);
        Bukkit.broadcastMessage("MAQUINA AAAAAAAAAAA");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!initialized) return;
        Block block = event.getBlockPlaced();
        if(block == null) return;
        if(!UtilNBT.contains(event.getItemInHand(), "Machine")) return;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            Date date = new Date();
            Machine machine = new Machine(-1, RankupConfiguration.MachineTypes.get(0),
                    CoreClientManager.get(event.getPlayer()).getAccountId(),
                    0, 0, 0,
                    block.getLocation(),
                    new Timestamp(date.getTime()),
                    new Timestamp(date.getTime())
            );
            machines.put(machine.getLocation(), machine);
            repository.createMachine(machine);
            event.getPlayer().sendMessage("Machine " + machine.getId() + " created!");
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block == null || block.getType() == Material.AIR) return;
        if(!machines.containsKey(block.getLocation())) return;
        event.setCancelled(true);
        Bukkit.broadcastMessage("MAQUINA AAAAAAAAAAA");
        // TODO: Abrir menu maquinas
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {

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

    public static MachineManager getContext() {
        return context;
    }
}