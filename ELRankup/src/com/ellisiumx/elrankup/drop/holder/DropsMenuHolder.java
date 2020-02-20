package com.ellisiumx.elrankup.drop.holder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class DropsMenuHolder implements InventoryHolder {

    public ItemStack item = null;
    public boolean upgradeMode = false;

    @Override
    public Inventory getInventory() {
        return null;
    }
}