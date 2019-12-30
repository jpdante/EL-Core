package com.ellisiumx.elrankup.crate.holder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CrateMenuHolder implements InventoryHolder {

    public String key;
    public String name;
    public CrateMenuType type;

    public CrateMenuHolder(String key, CrateMenuType type) {
        this.key = key;
        this.type = type;
    }

    public CrateMenuHolder(String key, String name, CrateMenuType type) {
        this.key = key;
        this.name = name;
        this.type = type;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public enum CrateMenuType {
        CreateMenu,
        EditMenu,
        CrateMenu,
    }
}