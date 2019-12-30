package com.ellisiumx.elrankup.crate.holder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CrateCreateMenuHolder implements InventoryHolder {

    public String key;
    public String name;

    public CrateCreateMenuHolder(String key, String name) {
        this.key = key;
        this.name = name;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
