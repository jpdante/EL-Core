package com.ellisiumx.elrankup.crate;

import com.ellisiumx.elcore.recharge.Recharge;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.crate.command.CrateCommand;
import com.ellisiumx.elrankup.machine.holders.MachineDropsMenuHolder;
import com.ellisiumx.elrankup.machine.holders.MachineFuelMenuHolder;
import com.ellisiumx.elrankup.machine.holders.MachineInfoMenuHolder;
import com.ellisiumx.elrankup.machine.holders.MachineMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class CrateManager implements Listener {

    private static CrateManager context;
    //private CrateManager repository;
    private ArrayList<Location> chests;

    public CrateManager(JavaPlugin plugin) {
        context = this;
        //repository = new CrateManager(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        /*for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("MachineTransactionFailure", "&f[&aMachines&f] &cFailed to transfer, please try again later. %ErrorMessage%");
            languageDB.insertTranslation("MachineNotEnoughMoney", "&f[&aMachines&f] &cYou don't have enough money to buy %MachineType%&c, it costs %Cost%");
            languageDB.insertTranslation("FuelNotEnoughMoney", "&f[&aMachines&f] &cYou don't have enough money to buy Fuel, it costs &a%Cost%");
            languageDB.insertTranslation("MachineLimitReached", "&f[&aMachines&f] &cMachine limit for %MachineType%&c has been reached!");
            languageDB.insertTranslation("MachineBought", "&f[&aMachines&f] &aMachine %MachineType%&a bought successfully!");
            languageDB.insertTranslation("MachineFuelBought", "&f[&aMachines&f] &aFuel bought successfully!");
            languageDB.insertTranslation("MachineUpgraded", "&f[&aMachines&f] &aMachine successfully upgraded!");
            languageDB.insertTranslation("MachineFullDrop", "&f[&aMachines&f] &cThe machine is already full and can no longer work, sell the drops to get it back to work.");
            languageDB.insertTranslation("MachineTankAlreadyFull", "&f[&aMachines&f] &cThe fuel tank of the machine is already full!");
            languageDB.insertTranslation("MachineTankReFull", "&f[&aMachines&f] &aThe machine has been replenished!");
            languageDB.insertTranslation("MachineDropsSold", "&f[&aMachines&f] &a%DropsAmount% drops were sold for %TotalPrice%, your new balance is %Balance%.");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();*/
        new CrateCommand(plugin);
    }

    /*@EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.FASTEST) return;

    }*/

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block == null || block.getType() != Material.CHEST) return;
        if(!chests.contains(block.getLocation())) return;

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        /*InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null) return;
        if (!(holder instanceof MachineMenuHolder)) return;
        event.setCancelled(true);
        if (!Recharge.use((Player) event.getWhoClicked(), "Crate", 400, false, false)) {
            event.getWhoClicked().sendMessage(UtilMessage.main("Crate", "You can't spam commands that fast."));
            return;
        }
        ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR) return;
        if (holder instanceof MachineFuelMenuHolder) {
            refuelMachine(event, itemStack, (MachineFuelMenuHolder) holder);
            return;
        }
        if (!UtilNBT.contains(itemStack, "MenuItem")) return;
        String command = UtilNBT.getString(itemStack, "MenuCommand");
        if (command == null) return;
        String[] args = command.split(" ", 2);
        if(args[0].equals("open")) {
            if (args.length < 2) return;
            openMenu((Player) event.getWhoClicked(), args[1], holder);
        } else if(args[0].equals("buymachine")) {
            if (args.length < 2) return;
            buyMachine((Player) event.getWhoClicked(), args[1]);
        } else if(args[0].equals("buyfuel")) {
            if (args.length < 2) return;
            buyFuel((Player) event.getWhoClicked(), args[1]);
        } else if(args[0].equals("upgrademachine")) {
            if (holder instanceof MachineInfoMenuHolder) {
                upgradeMachine((Player) event.getWhoClicked(), (MachineInfoMenuHolder) holder);
            }
        } else if(args[0].equals("selldrops")) {
            if (holder instanceof MachineDropsMenuHolder) {
                sellDrops((Player) event.getWhoClicked(), (MachineDropsMenuHolder) holder);
            }
        } else if(args[0].equals("close")) {
            event.getWhoClicked().closeInventory();
        }*/
    }

}
