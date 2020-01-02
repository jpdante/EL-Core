package com.ellisiumx.elrankup.crate;

import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.recharge.Recharge;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.crate.command.CrateCommand;
import com.ellisiumx.elrankup.crate.command.CrateTestCommand;
import com.ellisiumx.elrankup.crate.holder.CrateMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class CrateManager implements Listener {

    public static CrateManager context;
    public ArrayList<Integer> chests;
    public ArrayList<Crate> openCrates;

    public CrateManager(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        chests = new ArrayList<>();
        for(Location location : RankupConfiguration.CrateChestLocations) {
            chests.add(location.hashCode());
        }
        openCrates = new ArrayList<>();
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
        new CrateTestCommand(plugin);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.TICK) return;
        for(int i = 0; i < openCrates.size(); i++) {
            openCrates.get(i).animationTick();
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block == null || block.getType() != Material.CHEST) return;
        if(!chests.contains(block.getLocation().hashCode())) return;
        event.setCancelled(true);
        ItemStack itemStack = event.getPlayer().getItemInHand();
        if(itemStack == null || itemStack.getType() == Material.AIR) return;
        if (!UtilNBT.contains(itemStack, "CrateKey")) return;
        String crateName = UtilNBT.getString(itemStack, "CrateType");
        if (crateName == null) return;
        CrateType crateType = RankupConfiguration.getCrateTypeByName(crateName);
        if (crateType == null) return;
        if(itemStack.getAmount() > 1) {
            itemStack.setAmount(itemStack.getAmount() - 1);
            event.getPlayer().setItemInHand(itemStack);
        } else {
            event.getPlayer().setItemInHand(new ItemStack(Material.AIR, 1));
        }
        openCrates.add(new Crate(event.getPlayer(), (Chest) block.getState(), crateType).open());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof CrateMenuHolder)) return;
        CrateMenuHolder holder = (CrateMenuHolder) event.getInventory().getHolder();
        if(holder.type == CrateMenuHolder.CrateMenuType.CrateMenu) event.setCancelled(true);
        if (!Recharge.use((Player) event.getWhoClicked(), "Crate", 400, false, false)) {
            event.getWhoClicked().sendMessage(UtilMessage.main("Crate", "You can't spam commands that fast."));
            return;
        }
        ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR) return;
        if (!UtilNBT.contains(itemStack, "MenuItem")) return;
        event.setCancelled(true);
        String command = UtilNBT.getString(itemStack, "MenuCommand");
        if (command == null) return;
        String[] args = command.split(" ", 2);
        if(args[0].equals("confirm")) {
            if(holder.type == CrateMenuHolder.CrateMenuType.CreateMenu) {
                createCrate((Player) event.getWhoClicked(), event.getInventory(), holder);
            } else if(holder.type == CrateMenuHolder.CrateMenuType.EditMenu) {
                editCrate((Player) event.getWhoClicked(), event.getInventory(), holder);
            }
        }
    }

    public void createCrate(Player player, Inventory inventory, CrateMenuHolder holder) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for(ItemStack itemStack : inventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) continue;
            if (UtilNBT.contains(itemStack, "MenuItem")) continue;
            items.add(itemStack);
        }
        RankupConfiguration.CrateTypes.add(new CrateType(holder.key, holder.name, items));
        RankupConfiguration.save();
        player.sendMessage(ChatColor.GREEN + "Crate created successfully!");
        player.closeInventory();
    }

    public void editCrate(Player player, Inventory inventory, CrateMenuHolder holder) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for(ItemStack itemStack : inventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) continue;
            if (UtilNBT.contains(itemStack, "MenuItem")) continue;
            items.add(itemStack);
        }
        for(CrateType crateType : RankupConfiguration.CrateTypes) {
            if(!crateType.key.equalsIgnoreCase(holder.key)) continue;
            crateType.items = items;
        }
        RankupConfiguration.save();
        player.sendMessage(ChatColor.GREEN + "Crate edited successfully!");
        player.closeInventory();
    }

    public static ItemStack getCrateKey(CrateType crateType) {
        ItemStack itemStack = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(crateType.name.replace('&', ChatColor.COLOR_CHAR));
        itemStack.setItemMeta(itemMeta);
        itemStack = UtilNBT.set(itemStack, "true", "CrateKey");
        itemStack = UtilNBT.set(itemStack, crateType.key, "CrateType");
        return itemStack;
    }
}
