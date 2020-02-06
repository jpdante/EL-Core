package com.ellisiumx.elrankup.kit.holder;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;

public class KitMenuHolder implements InventoryHolder {

    public String kitName;
    public ArrayList<Rank> ranks;

    public KitMenuHolder(String kitName, ArrayList<Rank> ranks) {
        this.kitName = kitName;
        this.ranks = ranks;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
