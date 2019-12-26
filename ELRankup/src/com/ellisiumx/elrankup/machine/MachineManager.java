package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.recharge.Recharge;
import com.ellisiumx.elcore.timing.TimingManager;
import com.ellisiumx.elcore.utils.*;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.machine.command.MachineCommand;
import com.ellisiumx.elrankup.machine.holders.*;
import com.ellisiumx.elrankup.machine.repository.MachineRepository;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

public class MachineManager implements Listener {
    public boolean initialized;

    private static MachineManager context;
    private MachineRepository repository;
    private ArrayList<Machine> machines;
    private HashMap<Integer, Machine> machinesIds;
    private HashMap<Integer, MachineOwner> ownerMachines;
    private Inventory mainMenu;
    private Inventory shopMenu;

    public MachineManager(JavaPlugin plugin) {
        context = this;
        repository = new MachineRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initialized = false;
        ownerMachines = new HashMap<>();
        machinesIds = new HashMap<>();
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start("load machines");
            machines = repository.getMachines();
            for(Machine machine : machines) {
                machinesIds.put(machine.getId(), machine);
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
            languageDB.insertTranslation("MachineNotEnoughMoney", "&f[&aMachines&f] &cYou don't have enough money to buy %MachineType%&c, it costs %Cost%");
            languageDB.insertTranslation("FuelNotEnoughMoney", "&f[&aMachines&f] &cYou don't have enough money to buy Fuel, it costs &a%Cost%");
            languageDB.insertTranslation("MachineLimitReached", "&f[&aMachines&f] &cMachine limit for %MachineType%&c has been reached!");
            languageDB.insertTranslation("MachineBought", "&f[&aMachines&f] &aMachine %MachineType%&a bought successfully!");
            languageDB.insertTranslation("FuelBought", "&f[&aMachines&f] &aFuel bought successfully!");
            languageDB.insertTranslation("MachineFullDrop", "&f[&aMachines&f] &cThe machine is already full and can no longer work, sell the drops to get it back to work.");
            languageDB.insertTranslation("MachineTankAlreadyFull", "&f[&aMachines&f] &cThe fuel tank of the machine is already full!");
            languageDB.insertTranslation("MachineTankReFull", "&f[&aMachines&f] &aThe machine has been replenished!");
        }
        if(LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new MachineCommand(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!initialized) return;
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder == null) return;
        if(!(holder instanceof MachineMenuHolder)) return;
        event.setCancelled(true);
        if (!Recharge.use((Player)event.getWhoClicked(), "Machine", 400, false, false)) {
            event.getWhoClicked().sendMessage(UtilMessage.main("Machines", "You can't spam commands that fast."));
            return;
        }
        ItemStack itemStack = event.getCurrentItem();
        if(holder instanceof MachineFuelMenuHolder) {
            refuelMachine(event, itemStack, (MachineFuelMenuHolder)holder);
            return;
        }
        if(!UtilNBT.contains(itemStack, "MenuItem")) return;
        String command = UtilNBT.getString(itemStack, "MenuCommand");
        if(command == null) return;
        String[] args = command.split(" ", 2);
        if(args.length < 2) return;
        switch (args[0]) {
            case "open":
                openMenu((Player)event.getWhoClicked(), args[1], holder);
                break;
            case "buymachine":
                buyMachine((Player)event.getWhoClicked(), args[1]);
                break;
            case "buyfuel":
                buyFuel((Player)event.getWhoClicked(), args[1]);
                break;
            case "upgrademachine":
                upgradeMachine((Player)event.getWhoClicked(), (MachineInfoMenuHolder)holder);
                break;
            case "close":
                event.getWhoClicked().closeInventory();
                break;
        }
    }

    public void refuelMachine(InventoryClickEvent event, ItemStack itemStack, MachineFuelMenuHolder holder) {
        Player player = (Player) event.getWhoClicked();
        if(UtilNBT.contains(itemStack, "MenuItem")) {
            String command = UtilNBT.getString(itemStack, "MenuCommand");
            if(command == null) return;
            String[] args = command.split(" ", 2);
            if(args.length < 2) return;
            if(args[0].equals("open")) openMenu((Player)event.getWhoClicked(), args[1], holder);
            return;
        }
        if(holder.machine.getFuel() >= holder.machine.getType().getLevels().get(holder.machine.getLevel()).getMaxTank()) {
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineTankAlreadyFull")
                    .replace('&', ChatColor.COLOR_CHAR)
            );
            return;
        }
        if(!UtilNBT.contains(itemStack, "MachineFuel")) return;
        int liters = UtilNBT.getInt(itemStack, "Liters");
        int boost = UtilNBT.getInt(itemStack, "Boost");

        int maxFuel = holder.machine.getType().getLevels().get(holder.machine.getLevel()).getMaxTank();
        int resultItemStack = itemStack.getAmount();
        for(int i = 0; i < itemStack.getAmount(); i++) {
            int fuel = holder.machine.getFuel();
            int emptyFuel = maxFuel - fuel;
            if(emptyFuel <= 0) break;
            if(liters > emptyFuel) {
                holder.machine.setFuel(maxFuel);
                player.getInventory().addItem(getFuel(liters - emptyFuel, 1, boost));
                resultItemStack--;
                break;
            }
            holder.machine.setFuel(fuel + liters);
            resultItemStack--;
        }
        if(resultItemStack <= 0) {
            event.setCurrentItem(new ItemStack(Material.AIR, 1));
        } else {
            itemStack.setAmount(resultItemStack);
            event.setCurrentItem(itemStack);
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            repository.updateMachine(holder.machine);
        });
        player.sendMessage(
                LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineTankReFull")
                .replace('&', ChatColor.COLOR_CHAR)
        );
        player.closeInventory();
        player.openInventory(holder.machine.getFuelMenu());
    }

    public void openMenu(Player player, String menu, InventoryHolder holder) {
        player.closeInventory();
        String[] menuData = menu.split(" ", 2);
        switch (menuData[0]) {
            case "shop":
                player.openInventory(shopMenu);
                break;
            case "machines":
                player.openInventory(ownerMachines.get(CoreClientManager.get(player).getAccountId()).getMachinesMenu());
                break;
            case "machine":
                if(holder instanceof MachineFuelMenuHolder) {
                    player.openInventory(((MachineFuelMenuHolder) holder).machine.getMachineMenu());
                    break;
                }
                if(holder instanceof MachineDropsMenuHolder) {
                    player.openInventory(((MachineDropsMenuHolder) holder).machine.getMachineMenu());
                    break;
                }
                int machineID = Integer.parseInt(menuData[1]);
                Machine machine = machinesIds.get(machineID);
                if(machine == null) return;
                updateMachine(machine, player);
                player.openInventory(machine.getMachineMenu());
                break;
            case "permissions":
                //player.openInventory(mainMenu);
                break;
            case "friends":
                //player.openInventory(mainMenu);
                break;
            case "fuel":
                if(holder instanceof MachineInfoMenuHolder) {
                    player.openInventory(((MachineInfoMenuHolder) holder).machine.getFuelMenu());
                }
                break;
            case "drops":
                if(holder instanceof MachineInfoMenuHolder) {
                    player.openInventory(((MachineInfoMenuHolder) holder).machine.getDropsMenu());
                }
                break;
            case "main":
                player.openInventory(mainMenu);
                break;
        }
    }

    private void updateMachine(Machine machine, Player player) {
        MachineType.MachineLevel machineLevel = machine.getType().getLevels().get(machine.getLevel());
        if(machine.getDrops() > machineLevel.getMaxDropCount()) {
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineFullDrop")
                    .replace('&', ChatColor.COLOR_CHAR)
            );
            return;
        }
        if(machine.getFuel() > 0) {
            Date date = new Date();
            Timestamp lastMenuOpen = machine.getLastMenuOpen();
            Timestamp newMenuOpen = new Timestamp(date.getTime());
            long menuOpenDiference = newMenuOpen.getTime() - lastMenuOpen.getTime();
            long diffSeconds = menuOpenDiference / 1000 % 60;
            int dropDelay = machineLevel.getDropDelay();
            int dropMultiplier = 0;
            int fuel = machine.getFuel();
            for(int i = 0; i < machine.getFuel(); i++) {
                long result = diffSeconds - dropDelay;
                if(result <= 0) break;
                else {
                    diffSeconds = result;
                    dropMultiplier++;
                    fuel--;
                }
            }
            machine.setFuel(fuel);
            machine.setDrops(machine.getDrops() + dropMultiplier * machineLevel.getDropQuantity());
            machine.setLastMenuOpen(newMenuOpen);
            Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
                repository.updateMachine(machine);
            });
        }
    }

    public void upgradeMachine(Player player, MachineInfoMenuHolder holder) {
        player.closeInventory();
    }

    public void buyMachine(Player player, String machineData) {
        String[] data = machineData.split(" ", 2);
        MachineType machineType = RankupConfiguration.getMachineTypeByName(data[0]);
        CoreClient client = CoreClientManager.get(player);
        if(client.getRank().has(Rank.ALL) && ownerMachines.containsKey(client.getAccountId()) && ownerMachines.get(client.getAccountId()).getCountMachineType(machineType) >= 1) {
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineLimitReached")
                    .replaceAll("%MachineType%", machineType.getName())
                    .replace('&', ChatColor.COLOR_CHAR)
            );
            return;
        }
        float price = Float.parseFloat(data[1]);
        if(price > 0) {
            if(!EconomyManager.economy.has(player, price)) {
                player.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineNotEnoughMoney")
                        .replaceAll("%MachineType%", machineType.getName())
                        .replaceAll("%Cost%", String.valueOf(price))
                        .replace('&', ChatColor.COLOR_CHAR)
                );
                return;
            }
            EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, price);
            if(!response.transactionSuccess()) {
                player.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineTransactionFailure")
                        .replaceAll("%ErrorMessage%", response.errorMessage)
                        .replace('&', ChatColor.COLOR_CHAR)
                );
                return;
            }
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            Date date = new Date();
            Machine machine = new Machine(-1, machineType, client.getAccountId(), 0, 0, 0, new Timestamp(date.getTime()), new Timestamp(date.getTime()));
            repository.createMachine(machine);
            if(!ownerMachines.containsKey(client.getAccountId())) {
                MachineOwner machineOwner = new MachineOwner(client.getAccountId());
                machineOwner.addMachine(machine);
                ownerMachines.put(client.getAccountId(), machineOwner);
            }
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineBought")
                    .replaceAll("%MachineType%", machineType.getName())
                    .replace('&', ChatColor.COLOR_CHAR)
            );
        });
    }

    public void buyFuel(Player player, String fuelData) {
        String[] data = fuelData.split(" ");
        int liters = Integer.parseInt(data[0]);
        int amount = Integer.parseInt(data[1]);
        int boost = Integer.parseInt(data[2]);
        float price = Float.parseFloat(data[3]);
        if(price > 0) {
            if(!EconomyManager.economy.has(player, price)) {
                player.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "FuelNotEnoughMoney")
                        .replaceAll("%Cost%", String.valueOf(price))
                        .replace('&', ChatColor.COLOR_CHAR)
                );
                return;
            }
            EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, price);
            if(!response.transactionSuccess()) {
                player.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "MachineTransactionFailure")
                        .replaceAll("%ErrorMessage%", response.errorMessage)
                        .replace('&', ChatColor.COLOR_CHAR)
                );
                return;
            }
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            UtilInv.insert(player, getFuel(liters, amount, boost));
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "FuelBought")
                    .replace('&', ChatColor.COLOR_CHAR)
            );
        });
    }

    public static ItemStack getFuel(int liters, int amount, int boost) {
        ItemStack itemStack = RankupConfiguration.Fuel.clone();
        itemStack.setAmount(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replaceAll("%liters%", String.valueOf(liters)));
        if(itemMeta.getLore() != null) {
            ArrayList<String> lore = new ArrayList<>();
            for(String loreItem : itemMeta.getLore()) {
                lore.add(loreItem.replaceAll("%liters%", String.valueOf(liters)).replaceAll("%boost%", String.valueOf(boost)));
            }
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        itemStack = UtilNBT.set(itemStack, "true","MachineFuel");
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