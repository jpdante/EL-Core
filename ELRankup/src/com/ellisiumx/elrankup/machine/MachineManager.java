package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.Pair;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.repository.MachineRepository;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

public class MachineManager implements Listener {

    private MachineRepository repository;

    public MachineManager(JavaPlugin plugin) {
        repository = new MachineRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if(block == null || block.getType() == Material.AIR) return;
        if(!UtilNBT.contains(block, "Machine")) return;
        if(!UtilNBT.contains(block, "MachineID")) return;
        int machineID = UtilNBT.getInt("MachineID", -1);
        if(machineID == -1) return;
        event.setCancelled(true);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            Machine machine = repository.getMachine(machineID);
            repository.deleteMachine(machineID);
            event.getPlayer().sendMessage("Machine " + machine.getId() + " deleted!");
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if(block == null || block.getType() == Material.AIR) return;
        if(!UtilNBT.contains(block, "Machine")) return;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            Date date = new Date();
            Machine machine = new Machine(-1, RankupConfiguration.MachineTypes.get(0),
                    CoreClientManager.get(event.getPlayer()).getAccountId(),
                    0, 0, 0,
                    event.getBlockPlaced().getLocation(),
                    new Timestamp(date.getTime()),
                    new Timestamp(date.getTime())
            );
            repository.createMachine(machine);
            event.getPlayer().sendMessage("Machine " + machine.getId() + " created!");
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block == null || block.getType() == Material.AIR) return;
        if(!UtilNBT.contains(block, "Machine")) return;
        // TODO: Abrir menu maquinas
    }

    /*@EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOW) return;
        if (machineCreationStack.empty()) return;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            repository.createMachines(machineCreationStack);
        });
    }*/

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
