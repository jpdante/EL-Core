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
    private ArrayList<Pair<Integer, Integer>> levels;

    public MachineType(String key, String name, double price, ItemStack item, ItemStack drop, double dropPrice, ArrayList<Pair<Integer, Integer>> levels) {
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

    public ArrayList<Pair<Integer, Integer>> getLevels() {
        return levels;
    }
}
