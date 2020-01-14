package com.ellisiumx.elrankup.kit;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {

    private String name;
    private List<ItemStack> items;

    public Kit(String name, List<ItemStack> items) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
