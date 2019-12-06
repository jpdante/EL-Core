package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.utils.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MachineType {

    private int id;
    private String name;
    private double price;
    private ItemStack drop;
    private double dropPrice;
    private ArrayList<Pair<Integer, Integer>> levels;

    public MachineType(int id, String name, double price, ItemStack drop, double dropPrice, ArrayList<Pair<Integer, Integer>> levels) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.drop = drop;
        this.dropPrice = dropPrice;
        this.levels = levels;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
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
