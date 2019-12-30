package com.ellisiumx.elrankup.crate;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CrateType {

    public String key;
    public String name;
    public ArrayList<ItemStack> items;

    public CrateType(String key, String name, ArrayList<ItemStack> items) {
        this.key = key;
        this.name = name;
        this.items = items;
    }
}
