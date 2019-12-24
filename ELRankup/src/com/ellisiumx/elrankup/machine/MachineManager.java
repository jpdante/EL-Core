package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.timing.TimingManager;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.machine.command.MachineCommand;
import com.ellisiumx.elrankup.machine.holders.MachineMainMenuHolder;
import com.ellisiumx.elrankup.machine.holders.MachineShopMenuHolder;
import com.ellisiumx.elrankup.machine.repository.MachineRepository;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

public class MachineManager implements Listener {
    public boolean initialized;

    private static MachineManager context;
    private MachineRepository repository;
    private ArrayList<Machine> machines;
    private HashMap<Integer, MachineOwner> ownerMachines;
    private Inventory mainMenu;
    private Inventory shopMenu;

    public MachineManager(JavaPlugin plugin) {
        context = this;
        repository = new MachineRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initialized = false;
        ownerMachines = new HashMap<>();
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start("load machines");
            machines = repository.getMachines();
            for(Machine machine : machines) {
                if(!ownerMachines.containsKey(machine.getOwner())) {
                    MachineOwner machineOwner = new MachineOwner(machine.getOwner());
                    machineOwner.addMachine(machine);
                    ownerMachines.put(machine.getOwner(), machineOwner);
                } else ownerMachines.get(machine.getOwner()).addMachine(machine);
            }
            TimingManager.stop("load machines");
            UtilLog.log(Level.INFO, "[Machines] " + machines.size() + " machines loaded from mysql.");
            initialized = true;
        });
        mainMenu = RankupConfiguration.MainMenu.createMenu(new MachineMainMenuHolder());
        shopMenu = RankupConfiguration.ShopMenu.createMenu(new MachineShopMenuHolder());
        for(LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("MachineTransactionFailure", "&f[&aMachines&f] &cFailed to transfer, please try again later. %ErrorMessage%");
            languageDB.insertTranslation("MachineNotEnoughMoney", "&f[&aMachines&f] &cYou don't have enough money to buy %MachineType%, it costs %Cost%");
            languageDB.insertTranslation("MachineLimitReached", "&f[&aMachines&f] &cMachine limit for %Machine Type% has been reached!");
            languageDB.insertTranslation("MachineBought", "&f[&aMachines&f] &cMachine %MachineType% bought successfully!");
        }
        if(LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new MachineCommand(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!initialized) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder == null) return;
        if(!(holder instanceof MachineMainMenuHolder || holder instanceof MachineShopMenuHolder )) return;
        event.setCancelled(true);
        ItemStack itemStack = event.getCurrentItem();
        if(!UtilNBT.contains(itemStack, "MenuItem")) return;
        String command = UtilNBT.getString(itemStack, "MenuCommand");
        if(command == null) return;
        String[] args = command.split(" ", 2);
        switch (args[0]) {
            case "open":
                openMenu((Player)event.getWhoClicked(), args[1]);
                break;
            case "buymachine":
                buyMachine((Player)event.getWhoClicked(), args[1]);
                break;
            case "buyfuel":
                buyFuel((Player)event.getWhoClicked(), args[1]);
                break;
            case "close":
                event.getWhoClicked().closeInventory();
                break;
        }
    }

    public void openMenu(Player player, String menu) {
        player.closeInventory();
        switch (menu) {
            case "shop":
                player.openInventory(shopMenu);
                break;
            case "machines":
                Inventory inventory = ownerMachines.get(CoreClientManager.get(player).getAccountId()).getMachinesMenu();
                player.openInventory(inventory);
                break;
            case "permissions":
                //player.openInventory(mainMenu);
                break;
            case "friends":
                //player.openInventory(mainMenu);
                break;
            case "main":
                player.openInventory(mainMenu);
                break;
        }
    }

    public void buyMachine(Player player, String machineData) {
        String[] data = machineData.split(" ", 2);
        MachineType machineType = RankupConfiguration.getMachineTypeByName(data[0]);
        CoreClient client = CoreClientManager.get(player);
        if(client.getRank().has(Rank.ALL) && ownerMachines.get(client.getAccountId()).getCountMachineType(machineType) >= 1) {
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineLimitReached")
                    .replace("%MachineType%", machineType.getName())
            );
            return;
        }
        float price = Float.parseFloat(data[1]);
        if(price > 0) {
            if(!EconomyManager.economy.has(player, price)) {
                player.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineNotEnoughMoney")
                        .replace("%MachineType%", machineType.getName())
                        .replace("%Cost%", String.valueOf(price))
                );
                return;
            }
            EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, price);
            if(!response.transactionSuccess()) {
                player.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineTransactionFailure")
                        .replace("%ErrorMessage%", response.errorMessage)
                );
                return;
            }
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            Date date = new Date();
            Machine machine = new Machine(-1, machineType, client.getAccountId(), 0, 0, 0, new Timestamp(date.getTime()), new Timestamp(date.getTime()));
            repository.createMachine(machine);
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineBought")
                    .replace("%MachineType%", machineType.getName())
            );
        });
    }

    public void buyFuel(Player player, String fuel) {

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