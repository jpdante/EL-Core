package com.ellisiumx.elrankup.kit;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private String key;
    private String displayName;
    private int delay;
    private Rank rank;
    private List<ItemStack> items;

    public Kit(String key, String displayName, int delay, Rank rank, ArrayList<ItemStack> items) {
        this.key = key;
        this.displayName = displayName;
        this.delay = delay;
        this.rank = rank;
        this.items = items;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDelay() {
        return delay;
    }

    public Rank getRank() {
        return rank;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }
}
