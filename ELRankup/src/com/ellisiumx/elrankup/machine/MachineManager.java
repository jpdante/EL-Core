package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.timing.TimingManager;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.command.MachineCommand;
import com.ellisiumx.elrankup.machine.holders.MachineMainMenuHolder;
import com.ellisiumx.elrankup.machine.holders.MachineShopMenuHolder;
import com.ellisiumx.elrankup.machine.repository.MachineRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Level;

public class MachineManager implements Listener {

    private static MachineManager context;
    private boolean initialized;
    private MachineRepository repository;
    private ArrayList<Machine> machines;
    private Inventory mainMenu;
    private Inventory shopMenu;

    public MachineManager(JavaPlugin plugin) {
        context = this;
        repository = new MachineRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initialized = false;
        new MachineCommand(plugin);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start("load machines");
            machines = repository.getMachines();
            TimingManager.stop("load machines");
            UtilLog.log(Level.INFO, "[Machines] " + machines.size() + " machines loaded from mysql.");
            initialized = true;
        });
        mainMenu = RankupConfiguration.MainMenu.createMenu(new MachineMainMenuHolder());
        shopMenu = RankupConfiguration.ShopMenu.createMenu(new MachineShopMenuHolder());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder == null) return;
        if(!(holder instanceof MachineMainMenuHolder || holder instanceof MachineShopMenuHolder )) return;
        event.setCancelled(true);
        ItemStack itemStack = event.getCurrentItem();
        if(!UtilNBT.contains(itemStack, "MenuItem")) return;
        String command = UtilNBT.getString(itemStack, "MenuCommand");
        if(command == null) return;
        switch (command) {
            case "openshop":
                event.getWhoClicked().closeInventory();
                openShopMenu(event.getWhoClicked());
                break;
            case "openmachines":
                
                break;
            case "close":
                event.getWhoClicked().closeInventory();
                break;
            default:
                break;
        }
    }

    public void openMainMenu(HumanEntity player) {
        player.openInventory(mainMenu);
    }

    public void openShopMenu(HumanEntity player) {
        player.openInventory(shopMenu);
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


    /*@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
    }*/


    public static MachineManager getContext() {
        return context;
    }
}