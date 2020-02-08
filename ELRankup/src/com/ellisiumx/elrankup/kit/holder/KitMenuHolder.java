package com.ellisiumx.elrankup.kit.holder;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;

public class KitMenuHolder implements InventoryHolder {

    public String kitName;
    public Rank rank;
    public int delay;
    public boolean edit = false;

    public KitMenuHolder(String kitName, Rank rank, int delay) {
        this.kitName = kitName;
        this.rank = rank;
        this.delay = delay;
    }

    public KitMenuHolder(String kitName, boolean edit) {
        this.kitName = kitName;
        this.edit = edit;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
