package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.utils.Pair;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.holders.MachineDropsMenuHolder;
import com.ellisiumx.elrankup.machine.holders.MachineFuelMenuHolder;
import com.ellisiumx.elrankup.machine.holders.MachineInfoMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Machine {

    private int id;
    private MachineType type;
    private int owner;
    private int level;
    private int drops;
    private int fuel;
    private Timestamp lastMenuOpen;
    private Timestamp lastRefuel;

    public Machine(int id, MachineType type, int owner, int level, int drops, int fuel, Timestamp lastMenuOpen, Timestamp lastRefuel) {
        this.id = id;
        this.type = type;
        this.owner = owner;
        this.level = level;
        this.drops = drops;
        this.fuel = fuel;
        this.lastMenuOpen = lastMenuOpen;
        this.lastRefuel = lastRefuel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner() {
        return owner;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDrops() {
        return drops;
    }

    public void setDrops(int drops) {
        this.drops = drops;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public MachineType getType() {
        return type;
    }

    public Timestamp getLastMenuOpen() {
        return lastMenuOpen;
    }

    public Timestamp getLastRefuel() {
        return lastRefuel;
    }

    public void setLastMenuOpen(Timestamp lastMenuOpen) {
        this.lastMenuOpen = lastMenuOpen;
    }

    public void setLastRefuel(Timestamp lastRefuel) {
        this.lastRefuel = lastRefuel;
    }

    public String parseStringReplacer(String data) {
        data = data
                .replaceAll("%MachineLevel%", String.valueOf(level + 1))
                .replaceAll("%MachineDrops%", String.valueOf(drops))
                .replaceAll("%MachineFuel%", String.valueOf(fuel))
                .replaceAll("%MachineName%", type.getName().replace('&', ChatColor.COLOR_CHAR))
                .replaceAll("%MachineMaxFuel%", String.valueOf(type.getLevels().get(level).getMaxTank()))
                .replaceAll("%MachineDropQuantity%", String.valueOf(type.getLevels().get(level).getDropQuantity()))
                .replaceAll("%MachineDropDelay%", String.valueOf(type.getLevels().get(level).getDropDelay()))
                .replaceAll("%MachineMaxDropCount%", String.valueOf(type.getLevels().get(level).getMaxDropCount()));
        if(level + 1 >= type.getLevels().size()) data = data.replaceAll("%MachineUpgradeCost%", "-");
        else data = data.replaceAll("%MachineUpgradeCost%", String.valueOf(type.getLevels().get(level).getUpgradeCost()));
        return data;
    }

    public Inventory getMachineMenu() {
        Inventory inventory = RankupConfiguration.MachineInfoMenu.createMenu(new MachineInfoMenuHolder(this), "%MachineName%", type.getName());
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack == null) continue;
            if(itemStack.getType() == Material.STAINED_GLASS_PANE) continue;
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(parseStringReplacer(itemMeta.getDisplayName()));
            if(itemMeta.getLore() != null) {
                ArrayList<String> lore = new ArrayList<>();
                for(String data : itemMeta.getLore()) {
                    lore.add(parseStringReplacer(data));
                }
                itemMeta.setLore(lore);
            }
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);
        }
        return inventory;
    }

    public Inventory getFuelMenu() {
        Inventory inventory = RankupConfiguration.MachineFuelMenu.createMenu(new MachineFuelMenuHolder(this), "%MachineName%", type.getName());
        ArrayList<Pair<Integer, ItemStack>> progressBar = new ArrayList<>();
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack == null) continue;
            if(!UtilNBT.contains(itemStack, "MenuCommand")) continue;
            String command = UtilNBT.getString(itemStack, "MenuCommand");
            if(command == null || !command.equalsIgnoreCase("percentageslot")) continue;
            progressBar.add(new Pair<>(i, itemStack));
        }
        float percentage = fuel * 100.0f / (float) type.getLevels().get(level).getMaxTank();
        int quantity = (int)(progressBar.size() * percentage / 100.0f);
        for(int i = 0; i < quantity; i++) {
            ItemStack base = progressBar.get(i).getRight();
            ItemStack itemStack = new ItemStack(base.getType(), 1, (short)5);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(parseStringReplacer(base.getItemMeta().getDisplayName()).replaceAll("%FuelConsuption%", String.valueOf((int)percentage)));
            if(base.getItemMeta().getLore() != null) {
                ArrayList<String> lore = new ArrayList<>();
                for (String data : base.getItemMeta().getLore()) {
                    lore.add(parseStringReplacer(data).replaceAll("%FuelConsuption%", String.valueOf((int) percentage)));
                }
                itemMeta.setLore(lore);
            } else itemMeta.setLore(null);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            itemStack.setItemMeta(itemMeta);;
            inventory.setItem(progressBar.get(i).getLeft(), itemStack);
        }
        for(int i = quantity; i < (quantity + (progressBar.size() - quantity)); i++) {
            ItemStack base = progressBar.get(i).getRight();
            ItemStack itemStack = new ItemStack(base.getType(), 1, (short)14);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(parseStringReplacer(base.getItemMeta().getDisplayName()).replaceAll("%FuelConsuption%", String.valueOf((int)percentage)));
            if(base.getItemMeta().getLore() != null) {
                ArrayList<String> lore = new ArrayList<>();
                for (String data : base.getItemMeta().getLore()) {
                    lore.add(parseStringReplacer(data).replaceAll("%FuelConsuption%", String.valueOf((int) percentage)));
                }
                itemMeta.setLore(lore);
            } else itemMeta.setLore(null);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            itemStack.setItemMeta(itemMeta);;
            inventory.setItem(progressBar.get(i).getLeft(), itemStack);
        }
        return inventory;
    }

    public Inventory getDropsMenu() {
        Inventory inventory = RankupConfiguration.MachineDropsMenu.createMenu(new MachineDropsMenuHolder(this), "%MachineName%", type.getName());
        ArrayList<Pair<Integer, ItemStack>> progressBar = new ArrayList<>();
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack == null) continue;
            if(!UtilNBT.contains(itemStack, "MenuCommand")) continue;
            String command = UtilNBT.getString(itemStack, "MenuCommand");
            if(command == null || !command.equalsIgnoreCase("dropslot")) continue;
            progressBar.add(new Pair<>(i, itemStack));
        }
        float percentage = drops * 100.0f / (float) type.getLevels().get(level).getMaxDropCount();
        if(percentage > 100) percentage = 100.0f;
        int quantity = (int)(progressBar.size() * 64 * percentage / 100.0f);
        int set = 0;
        for(int i = 0; i < progressBar.size(); i++) {
            int amount = quantity - i * 64;
            if(amount > 64) amount = 64;
            if(amount <= 0) continue;
            set++;
            ItemStack base = progressBar.get(i).getRight();
            ItemStack itemStack = type.getDrop().clone();
            itemStack.setAmount(amount);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(parseStringReplacer(base.getItemMeta().getDisplayName()).replaceAll("%DropFill%", String.valueOf((int)percentage)));
            if(base.getItemMeta().getLore() != null) {
                ArrayList<String> lore = new ArrayList<>();
                for (String data : base.getItemMeta().getLore()) {
                    Bukkit.broadcastMessage(data);
                    lore.add(parseStringReplacer(data).replaceAll("%DropFill%", String.valueOf((int) percentage)));
                }
                itemMeta.setLore(lore);
            } else itemMeta.setLore(null);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            itemStack.setItemMeta(itemMeta);
            itemStack = UtilNBT.set(itemStack, "true", "MenuItem");
            itemStack = UtilNBT.set(itemStack, "selldrops", "MenuCommand");
            inventory.setItem(progressBar.get(i).getLeft(), itemStack);
        }
        for(int i = set; i < progressBar.size(); i++) {
            ItemStack base = progressBar.get(i).getRight();
            ItemStack itemStack = new ItemStack(base.getType(), 1, (short)8);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(parseStringReplacer(base.getItemMeta().getDisplayName()).replaceAll("%DropFill%", String.valueOf((int)percentage)));
            if(base.getItemMeta().getLore() != null) {
                ArrayList<String> lore = new ArrayList<>();
                for (String data : base.getItemMeta().getLore()) {
                    lore.add(parseStringReplacer(data).replaceAll("%DropFill%", String.valueOf((int) percentage)));
                }
                itemMeta.setLore(lore);
            } else itemMeta.setLore(null);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            itemStack.setItemMeta(itemMeta);;
            inventory.setItem(progressBar.get(i).getLeft(), itemStack);
        }
        return inventory;
    }
}
