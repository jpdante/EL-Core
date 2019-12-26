package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.utils.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MachineType {

    private String key;
    private String name;
    private double price;
    private ItemStack item;
    private ItemStack drop;
    private double dropPrice;
    private ArrayList<MachineLevel> levels;

    public MachineType(String key, String name, double price, ItemStack item, ItemStack drop, double dropPrice, ArrayList<MachineLevel> levels) {
        this.key = key;
        this.name = name;
        this.price = price;
        this.item = item;
        this.drop = drop;
        this.dropPrice = dropPrice;
        this.levels = levels;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemStack getDrop() {
        return drop;
    }

    public double getDropPrice() {
        return dropPrice;
    }

    public ArrayList<MachineLevel> getLevels() {
        return levels;
    }

    public static class MachineLevel {
        private int dropQuantity;
        private int dropDelay;
        private int maxTank;
        private int maxDropCount;
        private double upgradeCost;

        public MachineLevel(int dropQuantity, int dropDelay, int maxTank, int maxDropCount, double upgradeCost) {
            this.dropQuantity = dropQuantity;
            this.dropDelay = dropDelay;
            this.maxTank = maxTank;
            this.maxDropCount = maxDropCount;
            this.upgradeCost = upgradeCost;
        }

        public int getMaxDropCount() { return maxDropCount; }

        public int getDropQuantity() {
            return dropQuantity;
        }

        public int getDropDelay() {
            return dropDelay;
        }

        public int getMaxTank() {
            return maxTank;
        }

        public double getUpgradeCost() {
            return upgradeCost;
        }
    }
}
